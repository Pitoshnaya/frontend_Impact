Для работы с приведённым Makefile и Docker, сначала убедитесь, что у вас установлены следующие компоненты:

1. **Make**: Используйте команду `make --version` в терминале, чтобы проверить, установлен ли Make.
2. **Docker**: Выполните `docker --version`, чтобы убедиться, что Docker установлен.
3. **Docker Compose**: Введите `docker compose version`, чтобы проверить установку Docker Compose.

### Запуск команд из Makefile

1. **Запуск сайта**
   ```bash
   make up
   ```
   проверить на [localhost](http://localhost)

2. **Остановка сайта**
   ```bash
   make down
   ```

3. **Сборка сайта**
   ```bash
   make build
   ```

4. **Перезапуск сайта**
   ```bash
   make restart
   ```

5. **Просмотр логов сайта**
   ```bash
   make logs
   ```

6. **Очистка ресурсов**:
   ```bash
   make clean
   ``` 

<!-- ## Dev

1. **Запуск Development сервиса**:
   ```bash
   make up-dev
   ```

2. **Остановка Development сервиса**:
   ```bash
   make down-dev
   ```

3. **Сборка Development сервиса**:
   ```bash
   make build-dev
   ```

4. **Перезапуск Development сервиса**:
   ```bash
   make restart-dev
   ```

5. **Просмотр логов Development сервиса**:
    ```bash
    make logs-dev
    ```

6. **Запуск команд npm**:
    ```bash
    make run
    ```
-->
