package org.example.ahamed_jamal_umar_20221078_2330976;

class Article {
    private String category;
    private String title;
    private String description;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void appendDescription(String additionalDescription) {
        this.description = (this.description == null ? "" : this.description + "\n") + additionalDescription;
    }
}