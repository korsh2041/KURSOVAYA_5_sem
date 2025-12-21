-- Flyway migration: create basic tables users, books, logs

-- Enable pgcrypto for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users table
CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_type varchar(20) NOT NULL,
  name varchar(255) NOT NULL,
  email varchar(255),
  phone varchar(50),
  password_hash varchar(255) NOT NULL,
  additional_info text,
  registered_at timestamp NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_users_name ON users(name);

-- Books (catalog)
CREATE TABLE IF NOT EXISTS books (
  isbn varchar(50) PRIMARY KEY,
  title varchar(500) NOT NULL,
  author varchar(255),
  year int,
  publisher varchar(255),
  book_type varchar(100),
  additional_info text,
  created_at timestamp NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);

-- Logs (audit)
CREATE TABLE IF NOT EXISTS logs (
  id bigserial PRIMARY KEY,
  created_at timestamp DEFAULT now(),
  action varchar(255),
  details text
);
