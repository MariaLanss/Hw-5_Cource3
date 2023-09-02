select student.name, age, faculty.name from student left join faculty on student.faculty_id = faculty.id;


select student.* from student inner join avatar on student.id = avatar.student_id;
select student.* from student left join avatar on student.id = avatar.student_id;