package com.nash.phoenix.utils;

public class Status{
	private  int currTrack; //currTrackNumber from 0 - [tracklistView.size()-1]
	private int autodj; //determines which mode autodj is in (0: off, 1: mode1, 2: mode2, 3: mode3)
	private double oldVolume = 1.0; // last volume setting

	public Status(int _ct, int _ad){
		setCurrTrack(_ct);
		setAutodj(_ad);
	}
	
	public void rotateAutodj(){ //rotates Autodj-mode one further
		switch(getAutodj()){
		case 0: setAutodj(1); break;
		case 1: setAutodj(2); break;
		case 2: setAutodj(3); break;
		case 3: setAutodj(0); break;
		}
	}
	
	public int getCurrTrack() {
		return currTrack;
	}

	public void setCurrTrack(int currTrack) {
		this.currTrack = currTrack;
	}

	public int getAutodj() {
		return autodj;
	}

	public void setAutodj(int autodj) {
		this.autodj = autodj;
	}

	public double getOldVolume(){ return oldVolume; }
	public void setOldVolume(double v){ oldVolume = v; }
}
