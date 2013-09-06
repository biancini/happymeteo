package com.happymeteo.models;

import com.happymeteo.utils.Const;

public class CreateAccountDTO {
	public Const.CREATE_ACCOUNT_STATUS status;
	public String user_id;
	
	public CreateAccountDTO(Const.CREATE_ACCOUNT_STATUS status, String user_id) {
		this.status = status;
		this.user_id = user_id;
	}
}
