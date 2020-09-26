DROP TABLE IF EXISTS import_file_log;
DROP TABLE IF EXISTS import_folder_log;
DROP TABLE IF EXISTS users;

CREATE TABLE users  (
    id INT(11) NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    fullname VARCHAR(255) NOT NULL,
    rolename VARCHAR(255),
    PRIMARY KEY (id)
) CHARACTER SET utf8 COLLATE utf8_bin;

CREATE TABLE import_folder_log  (
    id INT(11) NOT NULL AUTO_INCREMENT,
    import_time DATETIME,
    folder_name VARCHAR(255),
    files_count INT,
    PRIMARY KEY (id)
) CHARACTER SET utf8 COLLATE utf8_bin;

CREATE TABLE import_file_log  (
    id CHAR(36) NOT NULL, -- Same as archie document id
    import_folder_id INT(11),
    import_time DATETIME,
    file_name VARCHAR(255),
    status_code int DEFAULT 0, -- 0 = in progress, positive = completed successfully, negative = invalid
    warning_message VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (import_folder_id) REFERENCES import_folder_log(id)
) CHARACTER SET utf8 COLLATE utf8_bin;