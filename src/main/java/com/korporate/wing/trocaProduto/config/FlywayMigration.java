package com.korporate.wing.trocaProduto.config;

import com.korporate.spring.persistence.multitenancy.TenantContext;
import com.korporate.spring.util.AmbienteEnum;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlywayMigration implements FlywayMigrationStrategy
{
    @Value("${ambiente}")
    private AmbienteEnum ambiente;

    @Override
    public void migrate(Flyway fw)
    {
        DataSource dataSource = fw.getConfiguration().getDataSource();

        // Migration no schema API
        String[] locationApi = new String[]{"flyway/migration/api"};
        Flyway
                .configure()
                .dataSource(dataSource)
                .schemas(TenantContext.TENANT_API)
                .locations(locationApi)
                .load()
                .migrate();

        // Migration dos tenants da aplicação
        String[] locationSistema = new String[]{"flyway/migration/sistema"};
        listAllTenant(dataSource).forEach(schema ->
                Flyway
                        .configure()
                        .dataSource(dataSource)
                        .schemas(schema)
                        .locations(locationSistema)
                        .load()
                        .migrate()
        );
    }

    // esse método aqui é uma cópia do KorporateApiService.listAllTenant(), mas que por conta do TransactionManager não podemos chama-lo aqui
    // (no momento que o flyway roda a aplicação ainda tá levantando e tem vários beans que ainda não foram instanciados, entre eles o TransactionManager)
    // (deu bronca no DataSource também, então usamos o do flyway que é "por fora" do Spring Data)
    private List<String> listAllTenant(DataSource dataSource)
    {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> tenantList = new ArrayList<>();
        String sql = "select distinct tenant from api.t_tenant where tenant <> 'api'";

        try
        {
            conn = DataSourceUtils.getConnection(dataSource);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next())
                tenantList.add(rs.getString(1));

            // se não tiver nada significa que é um banco limpo, então vamos rodar os scripts no schema public
            if (tenantList.size() == 0)
                tenantList.add("public");

        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            close(conn, ps, rs);
        }

        return tenantList;
    }


    public void close(Connection conn, PreparedStatement ps, ResultSet rs)
    {
        try
        {
            if (conn != null)
                conn.close();
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}