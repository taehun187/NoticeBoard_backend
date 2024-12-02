-- User 테이블 생성
CREATE TABLE IF NOT EXISTS user (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    username VARCHAR(20) NOT NULL UNIQUE,
                                    email VARCHAR(255) NOT NULL UNIQUE,
                                    password VARCHAR(255) NOT NULL,
                                    profile_image_url VARCHAR(255),
                                    phone VARCHAR(15),
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

-- Token 테이블 생성
CREATE TABLE IF NOT EXISTS token (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     token VARCHAR(255) NOT NULL
);

-- Post 테이블 생성
CREATE TABLE IF NOT EXISTS post (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    title VARCHAR(255) NOT NULL,
                                    content TEXT NOT NULL,
                                    type VARCHAR(50) DEFAULT 'default',
                                    likes INT DEFAULT 0,
                                    views INT DEFAULT 0,
                                    is_private BOOLEAN DEFAULT FALSE,
                                    is_comments_blocked BOOLEAN DEFAULT FALSE,
                                    file_path VARCHAR(255),
                                    user_id BIGINT NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                                    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Tag 테이블 생성
CREATE TABLE IF NOT EXISTS tag (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(50) NOT NULL UNIQUE
);

-- Post_Tag 관계 테이블 생성
CREATE TABLE IF NOT EXISTS post_tags (
                                         post_id BIGINT NOT NULL,
                                         tag_id BIGINT NOT NULL,
                                         PRIMARY KEY (post_id, tag_id),
                                         FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                                         FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

-- Comment 테이블 생성
CREATE TABLE IF NOT EXISTS comment (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       content TEXT NOT NULL,
                                       user_id BIGINT NOT NULL,
                                       post_id BIGINT NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
                                       FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                                       FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE
);
