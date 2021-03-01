package bean;

import lombok.Data;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2020/9/13 18:40
 */
@Data
public class Hero {
    private int id;

    private String name;

    @Override
    public String toString() {
        return "Hero{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public void hello(){
        System.out.println("hello");
    }
}
