package modelo.mybatis;

import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisUtil {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            String resource = "modelo/mybatis/mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream); // usa el environment por defecto del XML
            System.out.println("MyBatis inicializado con: " + resource);
        } catch (Exception e) {
            e.printStackTrace();
            sqlSessionFactory = null;
        }
    }

    public static SqlSession getSession() {
        if (sqlSessionFactory == null) {
            throw new IllegalStateException("SqlSessionFactory no inicializado - revisa mybatis-config.xml y la ruta del recurso");
        }
        return sqlSessionFactory.openSession();
    }

    // utilidad opcional si prefieres obtener la fábrica
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}