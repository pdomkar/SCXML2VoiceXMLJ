/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml.xslt;

public interface AssemblerResult<T> {

    public T result() throws IllegalStateException;

    public boolean isAvailable();

}
