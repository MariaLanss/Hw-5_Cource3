alter table student add constraint age_check check (age >= 16);


alter table student add constraint name_check unique (name);
alter table student alter column name set not null ;


alter table student alter age set default 20;


alter table faculty add constraint color_name_unique unique (color, name);