package com.huhuo.cmcar.car;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.huhuo.carservicecore.cust.car.ModelCar;
import com.huhuo.carservicecore.cust.car.ModelCarType;
import com.huhuo.carservicecore.cust.store.ModelStore;
import com.huhuo.cmcar.cartype.IServCarType;
import com.huhuo.cmsystem.SystemBaseCtrl;
import com.huhuo.cmsystem.store.IServStore;
import com.huhuo.integration.db.mysql.Condition;
import com.huhuo.integration.db.mysql.Dict;
import com.huhuo.integration.db.mysql.DictMgr;
import com.huhuo.integration.db.mysql.Dir;
import com.huhuo.integration.db.mysql.Order;
import com.huhuo.integration.db.mysql.Page;
import com.huhuo.integration.db.mysql.Where;
import com.huhuo.integration.web.JsonStore;
import com.huhuo.integration.web.Message;
import com.huhuo.integration.web.Message.Status;


@Controller("cmcarCtrlCar")
@RequestMapping(value="/cmcar/car")
public class CtrlCar extends SystemBaseCtrl {
	
	protected String basePath = "/car-module-car";
	
	@Resource(name = "cmcarServCar")
	private IServCar iservCar;
	@Resource(name = "cmcarServCarType")
	private IServCarType iServCarType;
	@Resource(name = "cmsystemServStore")
	private IServStore iServStore;
	
	/*************************************************************
	 * car management
	 *************************************************************/
	
	@RequestMapping(value="/index.do")
	public String index() {
		logger.debug("---> access car management page");
		return basePath + "/car/index";
	}
	
	@RequestMapping(value="/json-test.do")
	public String jsonTest(Model model) {
		List<ModelCar> records = iservCar.findByCondition(null, true);
		return render(model, new JsonStore<ModelCar>(records, records.size()));
	}
	
	@RequestMapping(value="/condition/get.do")
	public String get(Model model, Condition<ModelCar> condition, ModelCar t){
		if(t.getStatus() == null) {
			condition.setWhereList(new Where("status > ?", 0));
		}
		condition.setT(t);
		condition.setOrderList(new Order("createTime", Dir.DESC), new Order("updateTime", Dir.DESC));
		logger.debug("---> server receive: condition={}", condition);
		Page<ModelCar> page = condition.getPage();
		if(page == null) {
			page = new Page<ModelCar>();
		}
		List<ModelCar> records = iservCar.findByCondition(condition, true);
		model.addAttribute("records", records);
		page.setTotal(iservCar.countByCondition(condition));
		model.addAttribute("page", page);
		model.addAttribute("t", t);
		return basePath + "/car/page-grid";
	}
	
	@RequestMapping(value = "/typeahead/car.do")
	public String car(Model model, Condition<ModelCar> condition, ModelCar t) {
		condition.setT(t);
		List<ModelCar> records = iservCar.findByCondition(condition);
		return render(model, new JsonStore<ModelCar>(records, records.size()));
	}
	
	@RequestMapping(value = "/typeahead/cartype.do")
	public String carType(Model model, Condition<ModelCarType> condition, ModelCarType t) {
		condition.setT(t);
		List<ModelCarType> records = iServCarType.findByCondition(condition);
		return render(model, new JsonStore<ModelCarType>(records, records.size()));
	}
	
	@RequestMapping(value = "/typeahead/store.do")
	public String store(Model model, Condition<ModelStore> condition, ModelStore t) {
		condition.setT(t);
		List<ModelStore> records = iServStore.findByCondition(condition);
		return render(model, new JsonStore<ModelStore>(records, records.size()));
	}
	
	@RequestMapping(value = "/typeahead/dict.do")
	public String dict(Model model, String dictDisplayName) {
		List<Dict> records = DictMgr.match(ModelCar.GROUP_CUST_CAR_STATUS, dictDisplayName);
		return render(model, new JsonStore<Dict>(records, records.size()));
	}
	
	@RequestMapping(value="/add-ui.do")
	public String addUI(Model model) {
		logger.debug("==> access add ui");
		// prepare store for combo box of carTypeId
		List<ModelCarType> carTypeList = iServCarType.findByCondition(null);
		model.addAttribute("carTypeList", carTypeList);
		// prepare store for combo box of storeId
		List<ModelStore> storeList = iServStore.findByCondition(null);
		model.addAttribute("storeList", storeList);
		// prepare dictionary for combo box of color
		List<Dict> colorList = DictMgr.get(ModelCar.GROUP_CUST_CAR_COLOR);
		model.addAttribute("colorList", colorList);
		return basePath + "/car/add-ui";
	}
	
	@RequestMapping(value="/add.do")
	public void add(HttpServletResponse resp, ModelCar car, String icon) {
		logger.debug("---> server receive: car={}, icon={}", car, icon);
		// add new car
		iservCar.add(car);
		Message<ModelCar> msg = new Message<ModelCar>(Status.SUCCESS, "add new car success!", car);
		write(msg, resp);
	}
	
	@RequestMapping(value="/delete.do")
	public void delete(HttpServletResponse resp, @RequestParam(value="ids[]") List<Long> ids) {
		// receive data
		logger.debug("==> batch delete -->{}", ids);
		// retrieve model form DB
		iservCar.deleteBatch(ids);
		write(new Message<List<Long>>(Status.SUCCESS, "????????????", ids), resp);
	}
	
	@RequestMapping(value="/detail.do")
	public String detail(Model model, Long id) {
		logger.debug("==> edit ModeCar with id --> {}", id);
		model.addAttribute("car", iservCar.find(id));
		// prepare store for combo box of carTypeId
		List<ModelCarType> carTypeList = iServCarType.findByCondition(null);
		model.addAttribute("carTypeList", carTypeList);
		// prepare store for combo box of storeId
		List<ModelStore> storeList = iServStore.findByCondition(null);
		model.addAttribute("storeList", storeList);
		// prepare dictionary for combo box of color
		List<Dict> colorList = DictMgr.get(ModelCar.GROUP_CUST_CAR_COLOR);
		model.addAttribute("colorList", colorList);
		return basePath + "/car/detail";
	}
	
	@RequestMapping(value="/edit-ui.do")
	public String editUI(Model model, Long id) {
		logger.debug("==> edit ModeCar with id --> {}", id);
		model.addAttribute("car", iservCar.find(id));
		// prepare store for combo box of carTypeId
		List<ModelCarType> carTypeList = iServCarType.findByCondition(null);
		model.addAttribute("carTypeList", carTypeList);
		// prepare store for combo box of storeId
		List<ModelStore> storeList = iServStore.findByCondition(null);
		model.addAttribute("storeList", storeList);
		// prepare dictionary for combo box of color
		List<Dict> colorList = DictMgr.get(ModelCar.GROUP_CUST_CAR_COLOR);
		model.addAttribute("colorList", colorList);
		return basePath + "/car/edit-ui";
	}
	
	@RequestMapping(value="/update.do")
	public void update(HttpServletResponse resp, ModelCar t) {
		// retrieve model form DB
		iservCar.update(t);
		Message<ModelCar> msg = new Message<ModelCar>(Status.SUCCESS, "????????????", t);
		write(msg, resp);
	}
	
	@RequestMapping(value="/book.do")
	public void book(HttpServletResponse resp, ModelCar t, Date expireTime) {
		// book a car and setting expire time
		iservCar.book(t, expireTime);
		write(new Message<String>(Status.SUCCESS, "????????????"), resp);
	}
	
	/*************************************************************
	 * car trace business
	 *************************************************************/
	
	@RequestMapping(value="/trace/index.do")
	public String trace() {	// car trace management page
		logger.debug("---> access car trace page");
		return basePath + "/car/index";
	}
	@RequestMapping(value="/trace/trace.do")
	public String traceTemp() {	// car trace management page
		return basePath + "/trace/index";
	}
}
