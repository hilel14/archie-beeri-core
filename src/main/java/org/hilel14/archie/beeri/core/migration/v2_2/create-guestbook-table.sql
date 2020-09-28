CREATE TABLE guest_book  (
    id INT(11) NOT NULL AUTO_INCREMENT,
    doc_id CHAR(36) NOT NULL, -- Same as archie document id
    contact VARCHAR(255) DEFAULT NULL,
    remarks TEXT DEFAULT NULL,    
    creation_time DATETIME,
    status_code INT DEFAULT 0,
    PRIMARY KEY (id)
) CHARACTER SET utf8 COLLATE utf8_bin;
