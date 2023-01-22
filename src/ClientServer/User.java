package ClientServer;

import java.util.Objects;

public class User {
    private String name;
    private String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User() {
        this.name = "";
        this.password = "";
    }

    public static boolean isNameValid(String name) {
        if (name.isBlank()) return false;

        for (int i=0;i<name.length();i++) {
            char c = name.charAt(i);
            if (!(Character.isLetterOrDigit(c) || c == '_')) return false;
        }

        return true;
    }

    public static boolean isPasswordValid(String password) {
        if (password.isBlank()) return false;

        int charslow = 0, charsup = 0, nums = 0, specs = 0;

        for (int i=0;i<password.length();i++) {
            char c = password.charAt(i);

            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) charslow++;
                else charsup++;
            }
            else if (Character.isDigit(c)) nums++;
            else specs++;
        }

        return (charsup >= 1) && (charslow >= 3) && (nums >= 1) && (specs >= 1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
