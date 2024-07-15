CREATE TABLE `department` (
	`department_id`	int	NOT NULL AUTO_INCREMENT,
	`department` varchar(30) NOT NULL unique,
    PRIMARY KEY (`department_id`)
);

CREATE TABLE `member_info` (
	`member_id`	int NOT NULL auto_increment ,
	`name`	varchar(30)	NOT NULL,
	`gender` varchar(2) NOT NULL,
	`age` int NOT NULL,
	`department_id` int default NULL,
	`id` varchar(30) NOT NULL UNIQUE,
	`password` varchar(100) NOT NULL,
	`password_key` varchar(50) NOT NULL,
	`email` varchar(30) NOT NULL unique,
	`mode` varchar(5) NOT NULL,
    PRIMARY KEY (`member_id`),
    FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `book` (
    `book_id` int NOT NULL auto_increment,
    `bookname`	varchar(200) NOT NULL,
	`isbn`	bigint(13) NULL unique,
    `authors`	varchar(100) default NULL,
	`publisher`	varchar(200) default NULL,
    `book_image_URL` varchar(500) NOT NULL,
    `publication_year` int default null,
    `class_no` varchar(20) default null,
    `loan_count` int default null,
    PRIMARY KEY (`book_id`)
);

CREATE TABLE `search_history` (
	`search_history_id`	int NOT NULL AUTO_INCREMENT,
	`member_id` int NOT NULL,
	`book_id` int NOT NULL,
    `search_date` DateTime NOT NULL,
    PRIMARY KEY (`search_history_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member_info` (`member_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `favorite` (
	`favorite_id` int NOT NULL AUTO_INCREMENT,
	`member_id` int NOT NULL ,
	`book_id` int NOT NULL unique,
    `favorite_date` DateTime NOT NULL,
    PRIMARY KEY (`favorite_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member_info` (`member_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `book_count` (
	`book_count_id` int NOT NULL AUTO_INCREMENT,
	`department_id` int NOT NULL,
    `book_id` int NOT NULL,
	`book_count` int default 1,
    PRIMARY KEY (`book_count_id`),
    FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `popular_book` (
	`popular_id` int NOT NULL AUTO_INCREMENT,
	`book_id` int NOT NULL unique,
	`popular_date` DateTime default NULL,
    PRIMARY KEY (`popular_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `popular_book_members` (
    `popular_book_member_id` int NOT NULL AUTO_INCREMENT,
    `popular_id` int NOT NULL,
    `member_id` int NOT NULL,
    PRIMARY KEY (`popular_book_member_id`),
    FOREIGN KEY (`popular_id`) REFERENCES `popular_book` (`popular_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`member_id`) REFERENCES `member_info` (`member_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

create table `keyword` (
	`word` varchar(100) NOT NULL,
    `weight` int NOT NULL DEFAULT '0',
    PRIMARY KEY (`word`)
);

Insert into department values(1, '컴퓨터정보계열');
Insert into department values(2, 'IT온라인창업과');
Insert into department values(3, 'AI융합기계계열');
Insert into department values(4, '반도체전자계열');
Insert into department values(5, '무인항공드론과');
Insert into department values(6, '신재생에너지전기계열');
Insert into department values(7, '건축과');
Insert into department values(8, '인테리어디자인과');
Insert into department values(9, 'DIY실내장식과');
Insert into department values(10, '항공정비부사관과');
Insert into department values(11, '국방정보통신과');
Insert into department values(12, '의무/전투부사관과');
Insert into department values(13, '글로벌시스템융합과');
Insert into department values(14, '경영회계서비스계열');
Insert into department values(15, '호텔항공관광과');
Insert into department values(16, '사회복지과');
Insert into department values(17, '유아교육과');
Insert into department values(18, '간호학과');
Insert into department values(19, '보건의료행정과');
Insert into department values(20, '응급구조과');
Insert into department values(21, '동물보건과');
Insert into department values(22, '반려동물과');
Insert into department values(23, '조리제과제빵과');
Insert into department values(24, '콘텐츠디자인과');
Insert into department values(25, '만화애니메이션과');
