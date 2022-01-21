CREATE TABLE IF NOT EXISTS users (
    id       BIGSERIAL PRIMARY KEY,
    login    VARCHAR(255) NOT NULL UNIQUE ,
    password VARCHAR(255) NOT NULL,
    enabled  BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS authorities (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users_authorities (
    user_id      BIGINT REFERENCES users (id),
    authority_id BIGINT REFERENCES authorities (id),
    PRIMARY KEY (user_id, authority_id)
);

CREATE TABLE IF NOT EXISTS customer_contacts (
    id      BIGSERIAL PRIMARY KEY,
    email   VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    customer_id BIGINT REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    image_url   VARCHAR(255),
    parent_id   BIGINT REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS  products (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    image_url   VARCHAR(255),
    price       FLOAT NOT NULL,
    category_id BIGINT REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS orders (
    id           BIGSERIAL PRIMARY KEY,
    customer_id  BIGINT REFERENCES customer_contacts (id),
    price        FLOAT NOT NULL ,
    order_status SMALLINT NOT NULL ,
    shipping_method SMALLINT NOT NULL ,
    address      TEXT NOT NULL ,
    contact_email VARCHAR(255) NOT NULL ,
    creation_time TIMESTAMP NOT NULL ,
    deliver_time  TIMESTAMP,
    details       TEXT
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    orders_id BIGINT REFERENCES orders (id),
    product_id BIGINT REFERENCES products (id),
    price DOUBLE NOT NULL ,
    quantity INT
);


INSERT INTO users (login, password, enabled)
VALUES
    ('admin', '$2a$12$fIxG7VKFdJw9HriHgNyuNu.DitJytiDsERb25YAvhUEicllt37m0O', true),
    ('manager', '$2a$12$.z4y.gN6zGcUMjU/USKMEedIinnVn.4xGonlD1.M2213psnAWqYW.', true),
    ('user', '$2a$12$.z4y.gN6zGcUMjU/USKMEedIinnVn.4xGonlD1.M2213psnAWqYW.', true);

INSERT INTO customer_contacts (email, address)
VALUES ('admin@admin.ru', 'Электросталь, ул. Захарченко, 7А'),
        ('manager@manager.ru', 'Электросталь, ул. Второва, 3'),
        ('user@user.ru', 'Электросталь, Ул. Бульвар 60-летия Победы, 14');

INSERT INTO authorities (name)
VALUES
    ('ROLE_ADMIN'),
    ('ROLE_MANAGER'),
    ('ROLE_USER');

INSERT INTO users_authorities (user_id, authority_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);

INSERT INTO categories (title)
VALUES ('Электроника'),
       ('Бытовая техника');

INSERT INTO products(title, price, category_id)
VALUES ('Ноутбук Lenovo', 44990, 1),
       ('Телефон iPhone', 66490, 1),
       ('Стиральная машинка LG', 32290, 2),
       ('Телевизор Samsung', 32290, 1);
