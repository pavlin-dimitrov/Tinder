create TABLE `account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` timestamp,
  `created_by` varchar(255) DEFAULT NULL,
  `last_modified_at` timestamp,
  `last_modified_by` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `gender` varchar(255) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  CONSTRAINT account_pkey PRIMARY KEY (`id`)
);

insert into account(email, first_name, gender, last_name, password) values ('konov88@abv.bg', 'Kalin', 'MALE' ,'Konov',  '12345678');
insert into account(email, first_name, gender, last_name, password) values ('adams88@gmail.com', 'J.', 'FEMALE', 'Adams',  'dramaQueen');
