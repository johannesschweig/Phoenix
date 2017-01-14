package com.nash.phoenix.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nash.phoenix.App;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.nash.phoenix.utils.Track;

public class Tracklist {
	private ObservableList<Track> list;
	
	public Tracklist(){
		//ObservableList with extractor that fires an onChange-event when ActiveProperty is changed
		list = FXCollections.observableArrayList(track -> new Observable[]{track.activeProperty()});
	}
	
	public boolean isInTracklist(Track t){ //returns if a track is in the current tracklist
		for(Track i:list){
			if(i.getId()==t.getId()){
				return true;
			}
		}
		return false;
	}
	
	public String getCurrentIds(){ //returns the ids of the tracks in the tracklist
		String ids = "(";
		for(Track t:list){
			ids += " " + t.getId() + ",";
		}
		ids = ids.substring(0,ids.length()-1); //cuts of the last undesired ","
		ids += ")";
		return ids;
	}
	
	public Track getCurrentTrack(){
		int cr = App.mediaplayer.getStatus().getCurrTrack();
		return list.get(cr);
	}
	
	public void deleteTracks(int start, int end){ //deletes all Tracks from index a to index b
		int c_org = App.mediaplayer.getStatus().getCurrTrack();
		int deleted = end-start+1; //deleted Tracks
		//Delete from tracklist
		if(start!=end){
			list.remove(start, end+1);
		}else{
			list.remove(start);
		}
		int size_now = getSize();
		//update currTrack
		///case 1: tracks below cr are deleted (no change)
		///case 2: tracks above cr are deleted (recalc)
		///case 3: tracks including cr are deleted (recalc and new cr)
		///   -    new cr-preference: select tracks below/above/none
		if(start<=c_org){
			if(c_org<=end){ //case 3
				if(size_now>end){ //tracks below available
					App.mediaplayer.getStatus().setCurrTrack(start);
				}else if(start>0){ //tracks above available
					App.mediaplayer.getStatus().setCurrTrack(start-1);
				}else{ //none available
					App.mediaplayer.getStatus().setCurrTrack(-1);
				}
				App.mediaplayer.disposeOldPlayer();
			}else{
				App.mediaplayer.getStatus().setCurrTrack(c_org-deleted); //case 2
			}
		}

		App.coverPresenter.updateCoverView(false, "nothing");
	}

	public void addTracks(ArrayList<Integer> ids){
		list.addAll(App.database.getTracks(ids));
	}

	public void addTrack(Track track) {
		if(new File(track.getPath()).exists()){
			list.add(track);
			App.coverPresenter.updateCoverView(false, "nothing");
		}else{
			System.out.println("ERROR track ("+track.getPath()+") not existing");
		}
	}
	
	public void addTracks(List<Track> tracks){
		for(Track t:tracks){
			if(new File(t.getPath()).exists()){
				list.add(t);
			}else{
				System.out.println("ERROR track ("+t.getPath()+") not existing");
			}
		}
		App.coverPresenter.updateCoverView(false, "nothing");
	}
	
	public boolean isSubsequentTrack(int i){ //returns if there is a subsequent track in the tracklist
		return list.size() > 0 && list.size() > App.mediaplayer.getStatus().getCurrTrack() + i;
	}
	public boolean isPreviousTrack(int i) {//returns if there is a previous track (1:previous, 2:pre-previous) in the tracklist
		return App.mediaplayer.getStatus().getCurrTrack() >= i;
	}
	
	void setActive(int index, boolean value){ //sets the track with index an active of value
		list.get(index).setActive(value);
	}
	public int getRating(int index){ //returns rating of current song
		Track m = new Track(getPath(index));
		return m.getRating();
	}
	public ObservableList<Track> getList(){ //returns current tracklist
		return list;
	}
	String getPath(int index){ //gets the path of song at index
		return list.get(index).getPath();
	}
	public int getSize(){
		return list.size();
	}
	public BufferedImage getCover(int index){
		if(new File(getPath(index)).exists()){
			Track m = new Track(getPath(index));
			return m.getCover();
		}else{
			return null;
		}
	}

	void shuffle() {
		ArrayList<Track> tempList = new ArrayList<>();
		for(int i = App.mediaplayer.getStatus().getCurrTrack()+1; i<getSize(); i++){ //cycle through all upcoming tracks
			tempList.add(list.get(i));
		}
		list.subList(App.mediaplayer.getStatus().getCurrTrack()+1, getSize()).clear();
		Collections.shuffle(tempList); //shuffle them
		list.addAll(tempList);
	}

	void addAutodjTrack(int autodj) {
		Track newT = App.database.getRequestHandler().requestAutoDJTrack(getCurrentTrack(), autodj);
		if(newT!=null){
			addTrack(newT);
		}
	}
}