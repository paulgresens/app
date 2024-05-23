1. install docker

2. run
   `docker compose up`
3. tap into running containers to verify stuff
   `docker exec -it <CONTAINER_ID> /bin/bash`
4. inside database container
   `psql -h localhost -U postgres db`
   to use psql client