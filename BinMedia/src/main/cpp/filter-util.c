//
// Created by Administrator on 2022/1/18.
//
#include <stdio.h>
#include "filter-util.h"

/**
 *
 * @param des
 * @param in_gain 0.6
 * @param out_gain 0.3
 * @param delays 1000 (0 - 90000.0]
 * @param decays 0.5 (0 - 1.0]
 * @return
 * Make it sound as if there are twice as many instruments as are actually playing:
 * aecho=0.8:0.88:60:0.4
 * If delay is very short, then it sounds like a (metallic) robot playing music:
 * aecho=0.8:0.88:6:0.4
 * A longer delay will sound like an open air concert in the mountains:
 * aecho=0.8:0.9:1000:0.3
 * Same as above but with one more mountain:
 * aecho=0.8:0.9:1000|1800:0.3|0.25
 */
void * get_echo_filter_str(char* des,float in_gain,float out_gain,long delays,float decays){
    sprintf(des,arrayFilter[FILTER_ECHO].filter_str,in_gain,out_gain,delays,decays);
}

/**
 *
 * @param des
 * @param type in / out
 * @param start_sample 0
 * @param nb_samples 44100
 * @param start_time 0
 * @param duration 1...99999 sec
 * @return
 * Fade in first 15 seconds of audio:
 * afade=t=in:ss=0:d=15
 * Fade out last 25 seconds of a 900 seconds audio:
 * afade=t=out:st=875:d=25
 */
void * get_fade_filter_str(char* des,char * type,long start_sample,long nb_samples,long start_time,long duration){
    sprintf(des,arrayFilter[FILTER_FADE].filter_str,type,start_sample,nb_samples,start_time,duration);
}

/**
 *
 * @param des
 * @param volume
 * @return
 * Halve the input audio volume:
 * volume=volume=0.5
 * volume=volume=-6.0206dB
 * In all the above example the named key for volume can be omitted, for example like in:
 * volume=0.5
 * Increase input audio power by 6 decibels using fixed-point precision:
 * volume=volume=6dB:precision=fixed
 * Fade volume after time 10 with an annihilation period of 5 seconds:
 * volume='if(lt(t,10),1,max(1-(t-10)/5,0))':eval=frame
 */
void * get_volume_filter_str(char* des,float volume){
    sprintf(des,arrayFilter[FILTER_VOLUME].filter_str,volume);
}

/**
 *
 * @param des
 * @param hz 5.0 Range is 0.1 - 20000.0.
 * @param percentage
 * @return
 */
void * get_vibrato_filter_str(char* des,float hz,float percentage){
    sprintf(des,arrayFilter[FILTER_VIBRATO].filter_str,hz,percentage);
}

void build(int type){

}