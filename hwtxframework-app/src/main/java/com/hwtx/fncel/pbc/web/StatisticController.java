package com.hwtx.fncel.pbc.web;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.modules.sys.service.RegionService;
import com.hwtx.fncel.pbc.service.AppOrgService;
import com.hwtx.fncel.pbc.service.DataComputeService;
import com.hwtx.fncel.pbc.service.StatisticService;
import com.hwtx.fncel.pbc.util.AppConstants;
import com.hwtx.fncel.util.FncelUtils;
import com.hwtx.framework.interceptor.ModuleIocInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.web.BaseController;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Before(ModuleIocInterceptor.class)
@ControllerBind(controllerKey = "${adminPath}/app/statistic")
public class StatisticController extends BaseController {

    @Resource
    private StatisticService statisticService;
    @Resource
    private AppOrgService appOrgService;
    @Resource
    private DataComputeService dataComputeService;
    @Resource
    private RegionService regionService;

    static StatisticResultContainer statisticResultContainer = new StatisticResultContainer();

    @ActionKey(value = {"cp"})
    public void cp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        setAttr("date", simpleDateFormat.format(new Date()));
        setAttr("survey", getPara("survey"));
        render("/app/statistic/compute.jsp");
    }

    @ActionKey(value = {"doIt"})
    public void doIt() {

        Pair<Date, Integer> pair = beforeComputecheck();
        if (pair == null) {
            return;
        }
        final Date date = pair.getLeft();
        final Integer region = pair.getRight();
        final Map<Integer, String> areas = regionService.getAllAreasByProvince(region);
        if (areas.size() == 0) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR,
                    "无法获取 " + regionService.getProvince(region) + " 下全部县"));
            return;
        }

        final List<Integer> regions = Lists.newArrayList(areas.keySet());

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (Map.Entry<Integer, String> entry : areas.entrySet()) {
                    try {
                        logger.info("开始计算" + entry.getValue() + "");
                        statisticResultContainer.setRunning(entry.getValue());
                        statisticService.standardize(entry.getKey(), date,
                                AppConstants.Value_Inidata_Submit_Status, regions);
                        statisticResultContainer.getOver().add(entry.getValue());
                    } catch (Exception e) {
                        logger.error("{}", e);
                        statisticResultContainer.getError().add(entry.getValue());
                        statisticResultContainer.setErrorMessage(e.getMessage());
                        break;
                    }
                }
                statisticResultContainer.over();
            }
        });
        thread.setDaemon(true);
        thread.start();
        statisticResultContainer.start();
        renderJson(getRenderJson(Constants.RENDER_JSON_SUCCESS, "计算中"));
    }

    @ActionKey(value = {"getStatisticInfo"})
    public void getStatisticInfo() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("running", statisticResultContainer.getRunning());
        map.put("error", statisticResultContainer.getErrorMessage());
        map.put("error_region", statisticResultContainer.getError().size() != 0 ? "无法计算如下区域指标" + Joiner.on(",")
                .join(statisticResultContainer.getError()) : "");
        map.put("over", statisticResultContainer.isOver());
        renderJson(getRenderJson(map));
    }

    private Pair<Date, Integer> beforeComputecheck() {

        if (statisticResultContainer.isRunning()) {
            renderJson(getSuccessJson("正在计算 " + statisticResultContainer.getRunning() + "的指标值"));
            return null;
        }

        String date = getPara("startdate");
        Date startdate = null;
        try {
            startdate = FncelUtils.getDate(date);
        } catch (ParseException e) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "选择的日期不正确"));
            logger.error("{}", e);
            return null;
        }
        Integer region = appOrgService.getRegion();
        if (region == null) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, "用户区域不存在"));
            return null;
        }

        if (dataComputeService.checkSubmitDataExist(region, startdate,
                AppConstants.Value_Inidata_Handle_Status)) {
            renderJson(getRenderJson(Constants.RENDER_JSON_ERROR, date + "指标值已存在，无法再次计算和提交"));
            return null;
        }
        return new ImmutablePair(startdate, region);
    }

}