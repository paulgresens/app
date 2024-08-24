1. install docker

2. run
   `docker compose up`
3. tap into running containers to verify stuff
   `docker exec -it <CONTAINER_ID> /bin/bash`
4. inside database container
   `psql -h localhost -U postgres db`
   to use psql client



answers for assignment 2:
# Anfrage 1:
SELECT 'book' AS product_type, COUNT(*) AS count
FROM book
UNION ALL
SELECT 'cd' AS product_type, COUNT(*) AS count
FROM cd
UNION ALL
SELECT 'dvd' AS product_type, COUNT(*) AS count
FROM dvd;

# Anfrage 2:

with top_books as (
SELECT product_number, avg(rating) as avg_rating from rating
where product_number in (
select product_number from book
)
AND rating is not null
GROUP BY product_number
order by avg_rating desc
limit 5
),
top_dvds as (
SELECT product_number,avg(rating) as avg_rating from rating
where product_number in (
select product_number from dvd
)
AND rating is not null
GROUP BY product_number
order by avg_rating desc
limit 5
),

top_cds as (
SELECT product_number,avg(rating) as avg_rating from rating
where product_number in (
select product_number from cd
)
AND rating is not null
GROUP BY product_number
order by avg_rating desc
limit 5
)

SELECT 'book' AS product_type, product_number, avg_rating
FROM top_books
UNION ALL
SELECT 'cd' AS product_type, product_number, avg_rating
FROM top_cds
UNION ALL
SELECT 'dvd' AS product_type, product_number, avg_rating
FROM top_dvds;



# 3. Anfrage
SELECT title from product
where product_number not in
(select product_number from prod_condition where price is not null AND count > 0)


# 4. Anfrage
-- min price was not correctly constrained to be positive in the first submission
With ht as(SELECT product_number, max(price) as max_price, min(price) as min_price
FROM prod_condition
Group by product_number)

SELECT * from ht
Where max_price is not null
and min_price is not null
and max_price>2*min_price

# 5. Anfrage
SELECT DISTINCT product_number from rating as r1
WHERE EXISTS (
SELECT * from rating as r2
WHERE r1.product_number=r2.product_number
and r1.rating = 5
and r2.rating = 1
)


# 6. Anfrage
SELECT count(*) FROM product
WHERE product_number not in (
SELECT DISTINCT product_number FROM rating
)

# 7. Anfrage
SELECT customer_id
FROM rating
GROUP BY customer_id
HAVING COUNT(*) >= 10

# 8. Anfrage
SELECT DISTINCT name
FROM person_product
WHERE role = 'author'
AND EXISTS
(SELECT name
FROM person_product
WHERE role != 'author')
ORDER BY name ASC;

# 9. Anfrage
SELECT AVG(title_count) AS avg_title_count
FROM (
SELECT product_number, COUNT(title) AS title_count
FROM cd_title
GROUP BY product_number
) AS title_counts;

# 10. Anfrage
--top category id and name for a cateogry
CREATE OR REPLACE FUNCTION gettopcategory(pstartid INTEGER)
RETURNS TABLE (id INTEGER, name VARCHAR) AS $$
BEGIN
RETURN QUERY
WITH RECURSIVE top_category AS (
SELECT *
FROM category c
WHERE c.id = pstartid         
UNION ALL
SELECT c.*
FROM category c
INNER JOIN top_category ch
ON c.id = ch.super_category
)
SELECT
ch.id,
ch.name
FROM
top_category ch
WHERE
ch.super_category IS NULL;
END;
$$ LANGUAGE plpgsql;

--get all products pairs from similarity, that have an entry in product_category where their category is different
WITH similar_products_with_different_categories AS (
SELECT product_number_1, product_number_2 FROM
(SELECT
p1.id AS p1_category,
similarity.product_number_1,
p2.id AS p2_category,
similarity.product_number_2
FROM
similarity
JOIN
product_category p1 ON similarity.product_number_1 = p1.product_number
JOIN
product_category p2 ON similarity.product_number_2 = p2.product_number) AS similar_products_with_categories
WHERE EXISTS (
SELECT p1supercategory.id, p1supercategory.name, p2supercategory.id, p2supercategory.name
FROM gettopcategory(p1_category) AS p1supercategory
INNER JOIN
gettopcategory(p2_category) AS p2supercategory
ON p1supercategory.id != p2supercategory.id
)
)

--transform the result with the product pairs (with different categories) to be in 1 column and sort out duplicates
SELECT DISTINCT product_number FROM
(
SELECT product_number_1 AS product_number
FROM similar_products_with_different_categories
UNION
SELECT product_number_2 AS product_number
FROM similar_products_with_different_categories
) AS similar_products_with_different_categories_subquery



#11. Anfrage

SELECT product_number
FROM prod_condition
GROUP BY product_number
HAVING (COUNT(DISTINCT branch_office_id) = (SELECT COUNT(id) FROM branch_office))


# 12. Anfrage
WITH products_sold_in_all_stores AS (
SELECT product_number
FROM prod_condition
GROUP BY product_number
HAVING (COUNT(DISTINCT branch_office_id) = (SELECT COUNT(id) FROM branch_office))),

product_sold_in_all_stores_count AS (
SELECT COUNT (*) FROM products_sold_in_all_stores
),

products_sold_in_all_stores_cheapest_in_leipzig AS (
SELECT COUNT(prod_condition.product_number)
FROM prod_condition
JOIN (
SELECT product_number, MIN(price) as min_price
FROM prod_condition
WHERE price is not null
GROUP BY product_number
) AS min_prices
ON prod_condition.product_number = min_prices.product_number
AND prod_condition.price = min_prices.min_price
WHERE branch_office_id = (
SELECT id
FROM branch_office
WHERE name = 'Leipzig'
) AND prod_condition.product_number in (SELECT * FROM products_sold_in_all_stores) )


SELECT
CAST(products_sold_in_all_stores_cheapest_in_leipzig.count AS FLOAT) * 100 /
CAST(product_sold_in_all_stores_count.count AS FLOAT) AS percentage
FROM product_sold_in_all_stores_count, products_sold_in_all_stores_cheapest_in_leipzig;











--b)
-------------------------------------------------------------------------------


-- add new col avg rating
ALTER TABLE prod_condition
ADD COLUMN avg_rating Float;

-- def function to update avg rating
CREATE OR REPLACE FUNCTION update_avg_rating()
RETURNS TRIGGER AS $$
DECLARE
new_avg_rating FLOAT;
BEGIN
-- calculating new avg
SELECT AVG(rating) INTO new_avg_rating
FROM rating
WHERE product_number = NEW.product_number
AND product_condition = NEW.product_condition
AND branch_office_id = NEW.branch_office_id;


-- updating avg rating
UPDATE prod_condition
SET avg_rating = new_avg_rating
WHERE product_number = NEW.product_number
AND product_condition = NEW.product_condition
AND branch_office_id = NEW.branch_office_id;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- create trigger
CREATE TRIGGER after_insert_rating
AFTER INSERT ON rating
FOR EACH ROW
EXECUTE FUNCTION update_avg_rating();




---------------------------------------------------------------------------------------
--example insert
INSERT INTO rating (customer_id, product_number, branch_office_id, product_condition, rating, description,
rated_at, summary,marked_helpful_count)
Values(3, 'B00005RSCH', 3, 'unknown', 5, 'test', '2005-06-16 00:00:00', 'test',  0);
