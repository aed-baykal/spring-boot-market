CREATE TABLE IF NOT EXISTS customer_contacts (
     id      BIGSERIAL PRIMARY KEY,
     email   VARCHAR(255) NOT NULL,
     address TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id       BIGSERIAL PRIMARY KEY,
    login    VARCHAR(255) NOT NULL UNIQUE ,
    password VARCHAR(255) NOT NULL,
    enabled  BOOLEAN NOT NULL DEFAULT false,
    customer_id BIGINT REFERENCES customer_contacts (id)
);

CREATE TABLE IF NOT EXISTS registration_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expired_at TIMESTAMP NOT NULL,
    user_id BIGINT REFERENCES users(id)
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

CREATE TABLE IF NOT EXISTS categories (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    image_url   VARCHAR(255),
    parent_id   BIGINT REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS  products (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL UNIQUE,
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
    is_active    BOOLEAN DEFAULT TRUE,
    shipping_method SMALLINT NOT NULL ,
    address      TEXT NOT NULL ,
    contact_email VARCHAR(255) NOT NULL ,
    manager       VARCHAR(255) DEFAULT '',
    creation_time TIMESTAMP NOT NULL DEFAULT NOW(),
    deliver_time  TIMESTAMP,
    details       TEXT
);

CREATE TABLE IF NOT EXISTS order_items (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT REFERENCES orders (id),
    product_id  BIGINT REFERENCES products (id),
    price       DOUBLE NOT NULL ,
    quantity    INT NOT NULL,
    storage_status SMALLINT NOT NULL
);

CREATE TABLE IF NOT EXISTS  baners(
    id        BIGSERIAL PRIMARY KEY,
    title     VARCHAR(255) NOT NULL UNIQUE,
    image_url VARCHAR(255)
);

INSERT INTO customer_contacts (email, address)
VALUES ('admin@admin.ru', 'Электросталь, ул. Захарченко, 7А'),
       ('manager1@manager.ru', 'Электросталь, ул. Второва, 3'),
       ('manager2@manager.ru', 'Электросталь, ул. Захарченко, 7А'),
       ('marketUser@marketUser.ru', 'Электросталь, Ул. Бульвар 60-летия Победы, 14');

INSERT INTO users (login, password, enabled, customer_id)
VALUES
    ('admin', '$2a$12$fIxG7VKFdJw9HriHgNyuNu.DitJytiDsERb25YAvhUEicllt37m0O', true, 1),
    ('manager1', '$2a$12$.z4y.gN6zGcUMjU/USKMEedIinnVn.4xGonlD1.M2213psnAWqYW.', true, 2),
    ('manager2', '$2a$12$.z4y.gN6zGcUMjU/USKMEedIinnVn.4xGonlD1.M2213psnAWqYW.', true, 3),
    ('marketUser', '$2a$12$.z4y.gN6zGcUMjU/USKMEedIinnVn.4xGonlD1.M2213psnAWqYW.', true, 4);

INSERT INTO authorities (name)
VALUES
    ('ROLE_ADMIN'),
    ('ROLE_MANAGER'),
    ('ROLE_USER');

INSERT INTO users_authorities (user_id, authority_id)
VALUES (1, 1),
       (2, 2),
       (3, 2),
       (4, 3);

INSERT INTO categories (title, image_url)
VALUES ('Смартфоны и гаджеты', '/media/phones.jpeg'),
       ('Телевизоры, аудио, Hi-Fi', '/media/TV.jpeg'),
       ('Ноутбуки и компьютеры', '/media/Nout_Comp.jpeg'),
       ('Техника для кухни', '/media/KBT.png');

INSERT INTO products(title, price, category_id, image_url)
VALUES ('Смартфон Apple iPhone 11', 53999, 1, '/media/30052942bb.jpeg'),
       ('Смартфон Apple iPhone 12', 89999, 1, '/media/30052900bb.jpeg'),
       ('Смартфон Xiaomi Redmi 9T', 16999, 1, '/media/30055853bb.jpeg'),
       ('Смартфон Samsung Galaxy A32', 19999, 1, '/media/30056051bb.jpeg'),
       ('Смартфон Samsung Galaxy A12', 12999, 1, '/media/30054732bb.jpeg'),
       ('Смартфон Samsung Galaxy M12', 13999, 1, '/media/30057036bb.jpeg'),

       ('Телевизор Novex NWX-24H121MSY', 13999, 2, '/media/10025188bb.jpeg'),
       ('Телевизор Novex NWX-40F171MSY', 23999, 2, '/media/10025189bb.jpeg'),
       ('Телевизор Novex NWX-24H121WSY', 15299, 2, '/media/10025187bb.jpeg'),
       ('Телевизор Sony XR65A80J', 279999, 2, '/media/10026988bb.jpeg'),

       ('Ноутбук игровой ASUS TUF Gaming F15', 96999, 3, '/media/30058616bb.jpeg'),
       ('Ноутбук игровой ASUS TUF F15', 84999, 3, '/media/30058060bb.jpeg'),
       ('Ноутбук игровой ASUS ROG Strix G15', 99999, 3, '/media/30058617bb.jpeg'),
       ('Ноутбук игровой Acer Nitro 5', 94999, 3, '/media/30059619bb.jpeg'),
       ('Ноутбук игровой Acer Aspire 7', 75999, 3, '/media/30057652bb.jpeg'),
       ('Ноутбук игровой Lenovo Legion 5', 135999, 3, '/media/30061622bb.jpeg'),
       ('Ноутбук игровой MSI GF66 Katana ', 154999, 3, '/media/30058702bb.jpeg'),
       ('Ноутбук игровой ASUS ROG', 149999, 3, '/media/30057548bb.jpeg'),

       ('Встраиваемая индукционная панель Bosch Serie', 84999, 4, '/media/20048704bb.jpeg'),
       ('Микроволновая печь соло Gorenje', 3999, 4, '/media/20062339bb.jpeg'),
       ('Электрический духовой шкаф Gorenje', 14999, 4, '/media/20062243bb.jpeg'),
       ('Мультиварка Redmond RMC', 4199, 4, '/media/20052086bb.jpeg'),
       ('Погружной блендер Braun', 5999, 4, '/media/20064347bb.jpeg'),
       ('Морозильная камера Haier', 18999, 4, '/media/20038672b.jpeg');
