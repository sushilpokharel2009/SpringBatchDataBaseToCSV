package com.uvk.processor;

import org.springframework.batch.item.ItemProcessor;

import com.uvk.model.Vendor;

public class VendorItemProcessor implements ItemProcessor<Vendor, Vendor> {

	 //@Override
	 public Vendor process(Vendor vendor) throws Exception {
		 		 
	  return new Vendor();
	 }

	}