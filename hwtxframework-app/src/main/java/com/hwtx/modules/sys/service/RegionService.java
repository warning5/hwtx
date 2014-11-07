package com.hwtx.modules.sys.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtx.modules.sys.entity.CityArea;
import com.hwtx.modules.sys.entity.Province;
import com.hwtxframework.ioc.annotation.Component;
import com.hwtxframework.ioc.annotation.Dependon;
import com.jfinal.ext.plugin.sqlinxml.SqlKit;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
@Dependon("jFinalCache")
public class RegionService {

    private List<Province> provinces = Lists.newArrayList();

    private Map<Integer, List<CityArea>> cityMapping = Maps.newHashMap();
    private Map<Integer, List<CityArea>> areaMapping = Maps.newHashMap();
    private Map<Integer, String> provinceCodeAndName = Maps.newHashMap();
    private Map<Integer, String> cityCodeAndName = Maps.newHashMap();
    private Map<Integer, String> areaCodeAndName = Maps.newHashMap();

    public String getProvincesJson() {
        return JSON.toJSONString(getProvinces());
    }

    @PostConstruct
    public void init() {
        handleProvince();
        handleCityArea();
    }

    private void handleProvince() {
        if (provinces.size() == 0) {
            Province province = new Province();
            province.set("code", "");
            province.set("name", "所有省市");
            provinces.add(province);
            List<Province> ps = Province.dao.find(SqlKit.sql("region.getProvinces"));
            provinces.addAll(ps);
            for (Province p : ps) {
                provinceCodeAndName.put(p.getCode(), p.getName());
            }
        }
    }

    private void handleCityArea() {
        List<CityArea> cityAreas = CityArea.dao.find(SqlKit.sql("region.getCityAreas"));
        for (CityArea cityArea : cityAreas) {
            if (cityArea.getType() == 0) {
                cityCodeAndName.put(cityArea.getCode(), cityArea.getName());
                List<CityArea> cities = cityMapping.get(cityArea.getPcode());
                if (cities == null) {
                    cities = Lists.newArrayList();
                    CityArea city = new CityArea();
                    city.set("code", "");
                    city.set("name", "所有城市");
                    cities.add(city);
                }
                CityArea city = new CityArea();
                city.set("code", cityArea.getCode());
                city.set("name", cityArea.getName());
                cities.add(city);
                cityMapping.put(cityArea.getPcode(), cities);
            } else {
                areaCodeAndName.put(cityArea.getCode(), cityArea.getName());
                List<CityArea> areas = areaMapping.get(cityArea.getPcode());
                if (areas == null) {
                    areas = Lists.newArrayList();
                    CityArea area = new CityArea();
                    area.set("code", "");
                    area.set("name", "所有区县");
                    areas.add(area);
                }
                CityArea area = new CityArea();
                area.set("code", cityArea.getCode());
                area.set("name", cityArea.getName());
                areas.add(area);
                areaMapping.put(cityArea.getPcode(), areas);
            }
        }
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public String getCitiesJson(Integer provinceId) {
        return JSON.toJSONString(getCities(provinceId));
    }

    public List<CityArea> getCities(Integer provinceId) {
        return cityMapping.get(provinceId);
    }

    public String getAreasJson(Integer cityId) {
        return JSON.toJSONString(getCities(cityId));
    }

    public List<CityArea> getAreas(Integer cityId) {
        return areaMapping.get(cityId);
    }

    public List<Integer> getAreaCodes(Integer cityId) {
        List<Integer> result = Lists.newArrayList();
        for (CityArea cityArea : areaMapping.get(cityId)) {
            if (cityArea.getCode() != null) {
                result.add(cityArea.getCode());
            }
        }
        return result;
    }

    public String getProvince(Integer code) {
        return provinceCodeAndName.get(code);
    }

    public String getCity(Integer code) {
        return cityCodeAndName.get(code);
    }

    public String getArea(Integer code) {
        return areaCodeAndName.get(code);
    }

    public Map<Integer, String> getAllAreasByProvince(Integer province) {
        Map<Integer, String> areas = Maps.newHashMap();
        for (CityArea city : getCities(province)) {
            if (city.getCode() != null) {
                for (CityArea area : getAreas(city.getCode())) {
                    if (area.getCode() != null) {
                        areas.put(area.getCode(), area.getName());
                    }
                }
            }
        }
        return areas;
    }
}
