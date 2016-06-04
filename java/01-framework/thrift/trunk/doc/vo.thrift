namespace java service.demo 
 
struct Person {
  1: i32    id,
  2: string firstName,
  3: string email
}

struct Course {
  1: i32    id,
  2: Person student,
  3: list<Person> students
}