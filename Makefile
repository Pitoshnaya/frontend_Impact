# Docker

PROD_SERVICE = react-prod

up:
	docker compose up -d $(PROD_SERVICE)
down:
	docker compose down
build:
	docker compose build $(PROD_SERVICE)
restart:
	docker compose restart $(PROD_SERVICE)
logs:
	docker compose logs -f $(PROD_SERVICE)

DEV_SERVICE = react-dev

up-dev:
	docker compose up -d $(DEV_SERVICE)
down-dev:
	docker compose down
build-dev:
	docker compose build $(DEV_SERVICE)
restart-dev:
	docker compose restart $(DEV_SERVICE)
logs-dev:
	docker compose logs -f $(DEV_SERVICE)


run:
	pnpm install
	pnpm run dev

clean:
	docker compose down --rmi all -v

