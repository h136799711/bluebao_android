package com.itboye.bluebao.actiandfrag;

import com.itboye.bluebao.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * fragment home for tab home
 * @author Administrator
 *
 */
public class FragTabHome extends Fragment{
	
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	            Bundle savedInstanceState) {  
	        View layout_fragment_home = inflater.inflate(R.layout.layout_fragment_tab_home, container, false);  
	        return layout_fragment_home;  
	    }

}
