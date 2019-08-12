public class Student {
    private String matrics, name, course;

    public Student() {}

    public Student(String matrics, String name, String course) {
        this.matrics = matrics;
        this.name = name;
        this.course = course;
    }

    public String getMatrics() {
        return matrics;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }
}
