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
    PRIMARY KEY (`member_id`)
);

CREATE TABLE `search_history` (
	`search_history_id`	int	NOT NULL AUTO_INCREMENT,
	`member_id`	int	NOT NULL,
	`isbn`	int	NOT NULL,
	`bookname`	varchar(100) NULL,
	`authors`	varchar(30)	NULL,
	`publisher`	varchar(30)	NULL,
    `book_image_URL` varchar(50) NOT NULL,
    `search_date` DateTime NOT NULL,
    PRIMARY KEY (`search_history_id`)
);

CREATE TABLE `favorite` (
	`favorite_id`	int	NOT NULL AUTO_INCREMENT,
	`member_id`	int	NOT NULL,
	`isbn`	int	NOT NULL,
    PRIMARY KEY (`favorite_id`)
);

CREATE TABLE `keyword` (
	`keyword`	varchar(30)	NULL
);

CREATE TABLE `department` (
	`department_id`	int	NOT NULL AUTO_INCREMENT,
	`department`	varchar(30)	NOT NULL,
    PRIMARY KEY (`department_id`)
);

CREATE TABLE `recommand_book` (
	`recommand_id`	int	NOT NULL AUTO_INCREMENT,
	`member_id`	int	NOT NULL,
	`Field2`	varchar(100) NULL,
	`update_date` DateTime	NULL,
    PRIMARY KEY (`recommand_id`)
);

CREATE TABLE `book_count` (
	`book_count_id`	int	NOT NULL AUTO_INCREMENT,
	`department_id`	int	NOT NULL,
	`bookname`	varchar(100) NULL,
	`book_count` int	NULL,
    PRIMARY KEY (`book_count_id`)
);

ALTER TABLE `member_info` ADD CONSTRAINT `FK_department_TO_member_info_1` FOREIGN KEY (
	`department_id`
)
REFERENCES `department` (
	`department_id`
);

ALTER TABLE `search_history` ADD CONSTRAINT `FK_member_info_TO_search_history_1` FOREIGN KEY (
	`member_id`
)
REFERENCES `member_info` (
	`member_id`
);

ALTER TABLE `favorite` ADD CONSTRAINT `FK_member_info_TO_favorite_1` FOREIGN KEY (
	`member_id`
)
REFERENCES `member_info` (
	`member_id`
);

ALTER TABLE `recommand_book` ADD CONSTRAINT `FK_member_info_TO_recommand_book_1` FOREIGN KEY (
	`member_id`
)
REFERENCES `member_info` (
	`member_id`
);

ALTER TABLE `book_count` ADD CONSTRAINT `FK_department_TO_book_count_1` FOREIGN KEY (
	`department_id`
)
REFERENCES `department` (
	`department_id`
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



















