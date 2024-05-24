CREATE TABLE `department` (
	`department_id`	int	NOT NULL AUTO_INCREMENT,
	`department`	varchar(30)	NOT NULL,
    PRIMARY KEY (`department_id`)
);

CREATE TABLE `member_info` (
	`member_id`	int	NOT NULL auto_increment ,
	`name`	varchar(30)	NOT NULL,
	`gender`	varchar(2) NOT NULL,
	`age`	int	NOT NULL,
	`department_id`	int	NOT NULL,
	`id`	varchar(30)	NOT NULL UNIQUE,
	`password`	varchar(100)	NOT NULL,
    `password_key` varchar(50) NOT NULL,
	`email`	varchar(30)	NOT NULL,
	`mode`	varchar(5)	NOT NULL,
    PRIMARY KEY (`member_id`),
    FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`)
);

CREATE TABLE `book` (
    `book_id` int NOT NULL auto_increment,
    `bookname`	varchar(100) NOT NULL,
	`isbn`	int NULL,
    `authors`	varchar(30)	NULL,
	`publisher`	varchar(30)	NULL,
    `book_image_URL` varchar(500) NOT NULL,
    `publication_year` int null,
    `class_no` varchar(20) null,
    `loan_count` int null,
    PRIMARY KEY (`book_id`)
);

CREATE TABLE `search_history` (
	`search_history_id`	int	NOT NULL AUTO_INCREMENT,
	`member_id`	int	NOT NULL,
	`book_id` int NOT NULL,
    `search_date` DateTime NOT NULL,
    PRIMARY KEY (`search_history_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member_info` (`member_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
);

CREATE TABLE `favorite` (
	`favorite_id`	int	NOT NULL AUTO_INCREMENT,
	`member_id`	int	NOT NULL,
	`book_id` int NOT NULL,
    `favorite_date` DateTime NOT NULL,
    PRIMARY KEY (`favorite_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member_info` (`member_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
);


CREATE TABLE `book_count` (
	`book_count_id`	int	NOT NULL AUTO_INCREMENT,
	`department_id`	int	NOT NULL,
    `book_id` int NOT NULL,
	`book_count_date` datetime NOT NULL,
	`book_count` int	NULL,
    PRIMARY KEY (`book_count_id`),
    FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
);

CREATE TABLE `popular_book` (
	`popular_id`	int	NOT NULL AUTO_INCREMENT,
	`book_id` int NOT NULL,
	`popular_date` DateTime	NULL,
    PRIMARY KEY (`popular_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
);

CREATE TABLE `popular_book_members` (
    `popular_book_member_id` int NOT NULL AUTO_INCREMENT,
    `popular_id` int NOT NULL,
    `member_id` int NOT NULL,
    PRIMARY KEY (`popular_book_member_id`),
    FOREIGN KEY (`popular_id`) REFERENCES `popular_book` (`popular_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member_info` (`member_id`)
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

insert into search_history values(null,'1','1234567890','엄','준','식','https://i.namu.wiki/i/NpoCw6SZ0849_mTrqUxIMyTCLRK65S6MxuroLk46j2IzNs7uwW_iuw0b2KQX-lqpX06XVOEwa3LbTKOTh600Cw.webp','2020-02-02');
















