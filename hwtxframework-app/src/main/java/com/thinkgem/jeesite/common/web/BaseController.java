package com.thinkgem.jeesite.common.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Page1;
import com.thinkgem.jeesite.common.Constants;
import com.thinkgem.jeesite.common.datatable.BeanComparator;
import com.thinkgem.jeesite.common.datatable.JqGridResponse;
import com.thinkgem.jeesite.common.datatable.ServerResponseData;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class BaseController extends Controller {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected void addMessage(String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        setAttr(Constants.WEB_MESSAGE, sb.toString());
    }

    public String getRenderJson(String code, String message) {
        return "{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}";
    }

    public String getSuccessJson(String message) {
        return "{\"code\":\"" + Constants.RENDER_JSON_SUCCESS + "\",\"message\":\"" + message + "\"}";
    }

    public String getErrorRenderJson(String message, int statusCode) {
        getResponse().setStatus(statusCode);
        return "{\"code\":\"" + Constants.RENDER_JSON_ERROR
                + "\",\"message\":\"" + message + "\"}";
    }

    public String getRenderJson(Map<String, Object> pair) {
        return JSON.toJSONString(pair);
    }

    @SuppressWarnings("unchecked")
    @ActionKey(value = {"data"})
    public void data() {

        boolean paginate = getParaToBoolean("paginate", true);
        Page1<Object> page;
        if (paginate) {
            int offset = getOffset();
            page = getPageData(offset, getPageCount(offset));
        } else {
            page = getAllData();
        }
        sortAndSend(page);
    }

    protected void sortAndSendWithDateFormat(Page1<?> page, String dataFormat) {
        renderJson(JSON.toJSONStringWithDateFormat(getResponseData(page), dataFormat, SerializerFeature.WriteMapNullValue));
    }

    protected void sortAndSend(Page1<?> page) {
        renderJson(JSON.toJSONString(getResponseData(page), SerializerFeature.WriteMapNullValue));
    }

    private ServerResponseData<Object> getResponseData(Page1<?> page) {
        String sort0 = getPara("sSortDir_0");
        boolean asc = true;
        if (sort0 != null && !sort0.equals("")) {
            if (sort0.equals("asc")) {
                asc = true;
            } else {
                asc = false;
            }
        }
        ServerResponseData<Object> bizServerData = new ServerResponseData<>();
        bizServerData.setITotalDisplayRecords(page.getTotalRow());
        bizServerData.setITotalRecords(page.getTotalRow());
        bizServerData.setSEcho(getPara("sEcho"));
        List<Object> bizServerDatas = Lists.newArrayList();
        bizServerDatas.addAll(page.getList());

        String sortCol = getSortName();

        if (StringUtils.isNotEmpty(sortCol)) {
            Comparator mycmp1 = ComparableComparator.getInstance();
            if (!asc) {
                mycmp1 = ComparatorUtils.reversedComparator(mycmp1);
            }
            ArrayList<Object> sortFields = new ArrayList<>();
            sortFields.add(new BeanComparator(sortCol, mycmp1));
            ComparatorChain multiSort = new ComparatorChain(sortFields);
            Collections.sort(bizServerDatas, multiSort);
        }
        bizServerData.setAaData(bizServerDatas);
        return bizServerData;
    }

    protected int getOffset() {
        return getParaToInt("iDisplayStart", 0);
    }

    protected int getPageCount(int offset) {
        int iDisplayLength = getParaToInt("iDisplayLength", 90);
        return offset + iDisplayLength;
    }


    private String getSortName() {
        String sort = getPara("iSortCol_0");
        if (StringUtils.isNotEmpty(sort)) {
            return getSortName(sort);
        }
        return null;
    }

    protected String getSortName(String sort) {
        return null;
    }

    @ActionKey(value = {"jqGrid"})
    public void jqGrid() {

        int page = getParaToInt("page", 1);
        int rows = getParaToInt("rows", 20);
        String sort0 = getPara("sord");
        boolean asc = true;

        if (sort0 != null && !sort0.equals("")) {
            if (sort0.equals("asc")) {
                asc = true;
            } else {
                asc = false;
            }
        }

        Page<Object> data = getJqPageData(page, rows);
        JqGridResponse<Object> bizServerData = new JqGridResponse<>();
        bizServerData.setPage(data.getPageNumber());
        bizServerData.setTotal(data.getTotalPage());
        bizServerData.setRecords(data.getTotalRow());
        List<Object> bizServerDatas = Lists.newArrayList();
        bizServerDatas.addAll(data.getList());

        String sortCol = getSortName();

        if (StringUtils.isNotEmpty(sortCol)) {
            Comparator mycmp1 = ComparableComparator.getInstance();
            if (!asc) {
                mycmp1 = ComparatorUtils.reversedComparator(mycmp1);
            }
            ArrayList<Object> sortFields = new ArrayList<>();
            sortFields.add(new BeanComparator(sortCol, mycmp1));
            ComparatorChain multiSort = new ComparatorChain(sortFields);
            Collections.sort(bizServerDatas, multiSort);
        }

        bizServerData.setRows(bizServerDatas);
        renderJson(JSON.toJSONString(bizServerData, SerializerFeature.WriteMapNullValue));
    }

    protected <T> Page1<T> getPageData(int iDisplayStart, int fcount) {
        throw new RuntimeException("child class must be overwrite it.");
    }

    protected <T> Page1<T> getAllData() {
        throw new RuntimeException("child class must be overwrite it.");
    }

    protected <T> Page<T> getJqPageData(int page, int rows) {
        throw new RuntimeException("child class must be overwrite it.");
    }
}
