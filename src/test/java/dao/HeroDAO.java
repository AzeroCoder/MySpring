package dao;

import annotation.Component;
import bean.Hero;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 22:32
 */
@Component
public interface HeroDAO {
    Hero getHero(Integer id);
}
