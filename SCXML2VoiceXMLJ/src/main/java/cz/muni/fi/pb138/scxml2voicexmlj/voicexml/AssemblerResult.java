/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb138.scxml2voicexmlj.voicexml;

public interface AssemblerResult<T> {

    /**
     * Obtain the thing this assembler is building. Call {@code isAvailable()} to ensure
     * there exists valid value to be returned
     * @throws IllegalStateException if {@code isAvailable()} returns false
     */
    public T result() throws IllegalStateException;

    /**
     * Calling {@code result()} if this method returns false will result in an Exception
     * @return true if something can be obtained from the assembler ({@code result} can be queried),
     * false otherwise.
     */
    public boolean isAvailable();

}
