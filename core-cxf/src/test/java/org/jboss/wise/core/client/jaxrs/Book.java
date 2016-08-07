package org.jboss.wise.core.client.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Book")
public class Book {
    private String name;
    private long id;

    public Book() {
        init();
    }

    public Book(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public void setId(long i) {
        id = i;
    }

    public long getId() {
        return id;
    }

    final void init() {

    }

}
