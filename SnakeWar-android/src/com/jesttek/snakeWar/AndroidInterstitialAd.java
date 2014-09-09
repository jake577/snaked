package com.jesttek.snakeWar;

import com.jesttek.snakeWar.Inferfaces.IInterstitialAd;

public class AndroidInterstitialAd implements IInterstitialAd{
	
	@Override
	public void load() {
		MainActivity.UIThread.post(new Runnable(){
			@Override
			public void run() {
				MainActivity.loadInterstitialAd();
			}
		});
	}

	@Override
	public void show() {
		MainActivity.UIThread.post(new Runnable(){
			@Override
			public void run() {
				MainActivity.showInterstitialAd();	
			}
		});	
	}
}
