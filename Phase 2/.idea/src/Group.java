public class Group {
    private String gID;
    private String name;
    private String description;

    public void setgID(String gID) {
        this.gID = gID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getgID() {
        return gID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Group(String gID, String name, String description) {
        this.gID = gID;
        this.name = name;
        this.description = description;
    }
    
}
