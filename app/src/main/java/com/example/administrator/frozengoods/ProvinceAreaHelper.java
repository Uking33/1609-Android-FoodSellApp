package com.example.administrator.frozengoods;


import android.content.Context;
import android.content.res.AssetManager;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

class ProvinceAreaHelper{

    private Context mContext;

    /**
     * 所有省
     */
    private String[] mProvinceData;

    /**
     * key - 省 value - 市
     */
    private Map<String, String[]> mCityDataMap = new HashMap<>();

    /**
     * key - 市 values - 区
     */
    private Map<String, String[]> mDistrictDataMap = new HashMap<>();

    /**
     * key - 区 values - 邮编
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<String, String> mZipCodeDataMap = new HashMap<>();

    ProvinceAreaHelper(Context context){
        mContext = context;
    }

    /**
     * 解析省市区的XML数据
     */
    void initProvinceData(){
        List<ProvinceModel> provinceModelList;
        AssetManager assetManager = mContext.getAssets();
        try {
            InputStream input = assetManager.open("province_data.xml");

            //创建一个解析xml 的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHelper xmlParserHelper = new XmlParserHelper();

            //开始解析xml
            parser.parse(input,xmlParserHelper);
            input.close();

            //获取解析出来的数据
            provinceModelList = xmlParserHelper.getDataList();

            if(provinceModelList!=null){
                mProvinceData = new String[provinceModelList.size()];

                // 遍历所有省的数据
                for (int i=0; i< provinceModelList.size(); i++) {
                    mProvinceData[i] = provinceModelList.get(i).getName();
                    List<CityModel> cityList = provinceModelList.get(i).getCityList();
                    String[] cityNames = new String[cityList.size()];

                    // 遍历省下面的所有市的数据
                    for (int j=0; j< cityList.size(); j++) {
                        cityNames[j] = cityList.get(j).getName();
                        List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                        String[] districtNameArray = new String[districtList.size()];

                        // 遍历市下面所有区/县的数据
                        for (int k=0; k<districtList.size(); k++) {
                            DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipCode());

                            // 区/县对于的邮编，保存到mZipCodeDataMap
                            mZipCodeDataMap.put(districtList.get(k).getName(), districtList.get(k).getZipCode());
                            districtNameArray[k] = districtModel.getName();
                        }

                        // 市-区/县的数据，保存到mDistrictDataMap
                        mDistrictDataMap.put(cityNames[j], districtNameArray);
                    }

                    // 省-市的数据，保存到mCityDataMap
                    mCityDataMap.put(provinceModelList.get(i).getName(), cityNames);
                }
            }

        }catch (Exception e){
            MainActivity.MainAct.Message( "解析省市区的XML数据 Exception=" + e.getMessage());
            e.printStackTrace();
        }
    }

    String[] updateCities(String provinceName){
            return  mCityDataMap.get(provinceName);
    }

    String[] updateAreas(String cityName){
        return mDistrictDataMap.get(cityName);
    }

    String[] getProvinceData() {
        return mProvinceData;
    }
}

class XmlParserHelper extends DefaultHandler {

    /**
     * 存储所有的解析对象
     */
    private List<ProvinceModel> provinceList = new ArrayList<>();

    XmlParserHelper() {

    }

    List<ProvinceModel> getDataList() {
        return provinceList;
    }

    @Override
    public void startDocument() throws SAXException {
        // 当读到第一个开始标签的时候，会触发这个方法
    }

    private ProvinceModel provinceModel = new ProvinceModel();
    private CityModel cityModel = new CityModel();
    private DistrictModel districtModel = new DistrictModel();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // 当遇到开始标记的时候，调用这个方法
        switch (qName) {
            case "province":
                provinceModel = new ProvinceModel();
                provinceModel.setName(attributes.getValue(0));
                provinceModel.setCityList(new ArrayList<CityModel>());
                break;
            case "city":
                cityModel = new CityModel();
                cityModel.setName(attributes.getValue(0));
                cityModel.setDistrictList(new ArrayList<DistrictModel>());
                break;
            case "district":
                districtModel = new DistrictModel();
                districtModel.setName(attributes.getValue(0));
                districtModel.setZipCode(attributes.getValue(1));
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // 遇到结束标记的时候，会调用这个方法
        switch (qName) {
            case "district":
                cityModel.getDistrictList().add(districtModel);
                break;
            case "city":
                provinceModel.getCityList().add(cityModel);
                break;
            case "province":
                provinceList.add(provinceModel);
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    }
}

//城市的bean
class CityModel {
    private String name;
    private List<DistrictModel> districtList;

    CityModel() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    List<DistrictModel> getDistrictList() {
        return districtList;
    }

    void setDistrictList(List<DistrictModel> districtList) {
        this.districtList = districtList;
    }

    @Override
    public String toString() {
        return "CityModel [name=" + name + ", districtList=" + districtList
                + "]";
    }
}
//省份的bean
class ProvinceModel {
    private String name;
    private List<CityModel> cityList;

    ProvinceModel() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    List<CityModel> getCityList() {
        return cityList;
    }

    void setCityList(List<CityModel> cityList) {
        this.cityList = cityList;
    }

    @Override
    public String toString() {
        return "ProvinceModel [name=" + name + ", cityList=" + cityList + "]";
    }
}
//区域的bean
class DistrictModel {
    private String name;
    private String zipCode;

    DistrictModel() {
        super();
    }

    DistrictModel(String name, String zipCode) {
        super();
        this.name = name;
        this.zipCode = zipCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getZipCode() {
        return zipCode;
    }

    void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "DistrictModel [name=" + name + ", zipCode=" + zipCode + "]";
    }
}

