package com.huhuo.cmsystem.dict;

import java.util.List;

import com.huhuo.carservicecore.constant.Dictionary.ModelDict;
import com.huhuo.carservicecore.constant.Dictionary.ModelDictGroup;
import com.huhuo.carservicecore.sys.dictionary.ModelDictionary;
import com.huhuo.integration.base.IBaseExtenseServ;


public interface IServDictionary extends IBaseExtenseServ<ModelDictionary> {
	/**
	 * get a group of dictionary by group name
	 * @param dictGroup
	 * @return
	 */
	List<ModelDictionary> getGroupsBy(ModelDictGroup dictGroup);
	/**
	 * get a single dictionary by group name and dictKey
	 * @param dictGroup
	 * @param dictKey
	 * @return null if there's not suitable one
	 */
	ModelDictionary getBy(ModelDictGroup dictGroup, Integer dictKey);
	/**
	 * get a dictionary by enum type dict
	 * @param dict
	 * @return
	 */
	ModelDictionary getBy(ModelDict dict);
}
