package it.nextre.corsojava.entity;

public class Role extends Entity {
    private String role;
    private Long priority;


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }


}
