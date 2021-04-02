/********************************************************************************************************
 * @file     OnOffStatusMessage.java 
 *
 * @brief    for TLSR chips
 *
 * @author	 telink
 * @date     Sep. 30, 2010
 *
 * @par      Copyright (c) 2010, Telink Semiconductor (Shanghai) Co., Ltd.
 *           All rights reserved.
 *           
 *			 The information contained herein is confidential and proprietary property of Telink 
 * 		     Semiconductor (Shanghai) Co., Ltd. and is available under the terms 
 *			 of Commercial License Agreement between Telink Semiconductor (Shanghai) 
 *			 Co., Ltd. and the licensee in separate contract or the terms described here-in. 
 *           This heading MUST NOT be removed from this file.
 *
 * 			 Licensees are granted free, non-transferable use of the information in this 
 *			 file under Mutual Non-Disclosure Agreement. NO WARRENTY of ANY KIND is provided. 
 *           
 *******************************************************************************************************/
package com.telink.ble.mesh.core.message.generic;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.telink.ble.mesh.core.message.StatusMessage;

import org.spongycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * Created by kee on 2019/8/20.
 */

public class VendorMessage extends StatusMessage implements Parcelable {

    private static final int DATA_LEN_COMPLETE = 3;


    private byte[] dataParam = new byte[8];

    public VendorMessage() {
    }


    protected VendorMessage(Parcel in) {

        in.readByteArray(dataParam);

    }

    public static final Creator<VendorMessage> CREATOR = new Creator<VendorMessage>() {
        @Override
        public VendorMessage createFromParcel(Parcel in) {
            return new VendorMessage(in);
        }

        @Override
        public VendorMessage[] newArray(int size) {
            return new VendorMessage[size];
        }
    };

    @Override
    public void parse(byte[] params) {
       String tmp = ByteUtils.toHexString(params);
        Log.i("test","params===>"+tmp);
       System.arraycopy(params,0,dataParam,0,dataParam.length);
        Log.i("test","params===>"+ByteUtils.toHexString(dataParam));
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(dataParam);

    }


    public byte[] getDataParam() {
        return dataParam;
    }

    public void setDataParam(byte[] dataParam) {
        this.dataParam = dataParam;
    }
}
