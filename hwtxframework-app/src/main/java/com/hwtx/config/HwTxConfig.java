package com.hwtx.config;

import com.google.common.collect.Maps;
import com.hwtx.framework.plugins.ModuleIocPlugin;
import com.hwtx.framework.plugins.ModuleManagerPlugin;
import com.hwtx.framework.plugins.ModuleSqlInXmlPlugin;
import com.jfinal.config.*;
import com.jfinal.core.HwTx;
import com.jfinal.core.HwTxAutoBindRoutes;
import com.jfinal.core.HwTxAutoTableBindPlugin;
import com.jfinal.ext.plugin.sqlinxml.SqlInXmlPlugin;
import com.jfinal.plugin.activerecord.IContainerFactory;
import com.jfinal.plugin.activerecord.tx.TxByActionMethods;
import com.jfinal.plugin.activerecord.tx.TxByRegex;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.JspRender;
import com.jfinal.render.ViewType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HwTxConfig extends JFinalConfig {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String adminPath = "adminPath";

    public void configConstant(Constants me) {
        loadPropertyFile("jeesite.properties");
        me.setDevMode(getPropertyToBoolean("devMode", false));
        me.setViewType(ViewType.JSP);
        JspRender.setSupportActiveRecord(false);
        String baseViewPath = getProperty("baseViewPath");
        if (baseViewPath != null && !baseViewPath.equals(""))
            me.setBaseViewPath(baseViewPath);
    }

    public void configPlugin(Plugins me) {
        DruidPlugin druid = new DruidPlugin(getProperty("jdbc.url"), getProperty("jdbc.username"),
                getProperty("jdbc.password"));
        me.add(druid);
        me.add(new ModuleManagerPlugin());
        me.add(new SqlInXmlPlugin());
        HwTxAutoTableBindPlugin hwTxAutoTableBindPlugin = new HwTxAutoTableBindPlugin(druid);
        hwTxAutoTableBindPlugin.setContainerFactory(new IContainerFactory() {
            public Map<String, Object> getAttrsMap() {return new HashMap<String, Object>();}
            public Map<String, Object> getColumnsMap() {return Maps.newLinkedHashMap();}
            public Set<String> getModifyFlagSet() {return new HashSet<String>();}
        });
        me.add(hwTxAutoTableBindPlugin);
        me.add(new ModuleIocPlugin("ioc/*.xml"));
        me.add(new ModuleSqlInXmlPlugin());
    }

    public void configInterceptor(Interceptors me) {
        me.add(new TxByRegex("save*", false));
        me.add(new TxByRegex("update*", false));
        me.add(new TxByRegex("delete*", false));
        me.add(new TxByActionMethods("save", "update"));
    }

    public void configHandler(Handlers me) {
    }

    protected static long start;

    @Override
    public void afterJFinalStart() {
        logger.info("start framework spend " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void beforeJFinalStart() {
        start = System.currentTimeMillis();
    }

    @Override
    public void configRoute(Routes me) {
        HwTxAutoBindRoutes hwTxAutoBindRoutes = HwTx.getManagedBean(HwTxAutoBindRoutes.class);
        hwTxAutoBindRoutes.init();
        me.add(hwTxAutoBindRoutes);
    }
}
