/*
 * Sequence.java
 *
 * Created on August 27, 2006, 4:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * Copyright 2006 by Jon A. Webb
 *     This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the Lesser GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jjil.core;

/**
 * Sequence is used to construct a sequence of image processing pipeline
 * stages.
 *
 * Each Sequence object contains a PipelineStage, which can be null,
 * and a Sequence object, which can also be null. The PipelineStage
 * is the head of the list of PipelineStage's starting here, and
 * the Sequence object is the rest of the list. The Sequence object
 * can be non-null only if the PipelineStage object is non-null.
 * <p>
 * Hungarian prefix is 'seq'.
 * @author webb
 */
public class Sequence extends PipelineStage {
    private PipelineStage pFirst;
    private Sequence pNext;
    
    /** Creates a new instance of Sequence with no pipeline. */
    public Sequence() {
    }
    
    /** Creates a new instance of Sequence with a single 
     *  PipelineStage.
     *
     * @param p the PipelineStage.
     */
    public Sequence(PipelineStage p) {
        this.pFirst = p;
    }
    
    /** Add an additional PipelineStage at the end of the
     * current Sequence.
     *
     * @param p the PipelineStage to be added.
     */
    public void Add(PipelineStage p)
    {
        if (this.pFirst == null) {
            this.pFirst = p;
        } else {
            if (this.pNext == null) {
                this.pNext = new Sequence(p);
            } else {
                this.pNext.Add(p);
            }
        } 
    }
    
    /** Returns true iff the pipeline has no image available
     *
     * @return true iff the pipeline has no image available.
     */
    public boolean Empty()
    {
        if (pNext == null) {
            return pFirst.Empty();
        } else {
            return pNext.Empty();
        }
    }
    
    /** Returns the Image produced by the last stage
     * in the pipeline. Overrides PipelineStage.Front.
     *
     * @return the Image produced by the pipeline.
     */
    public Image Front()
    {
        if (pNext == null) {
            return pFirst.Front();
        } else {
            return pNext.Front();
        }
    }
    
    /** Process an image by the pipeline.
     * The image is pushed onto the beginning of the pipeline,
     * and then each stage's output is passed to the next
     * stage, until the end of the pipeline is reached.
     * Overrides PipelineStage.Push(Image).
     *
     * @param i the image to be pushed.
     * @throws IllegalStateException if the pipeline is empty
     *    when this function is called, or a pipeline stage
     *    did not return a result and is not the last stage
     *    in the pipeline.
     */
    public void Push(Image i) throws IllegalStateException
    {
        if (pFirst == null) {
            throw new IllegalStateException(
                    Messages.getString("Sequence.0") + //$NON-NLS-1$
                    Messages.getString("Sequence.1") //$NON-NLS-1$
                    );
        }
        pFirst.Push(i);
        if (pFirst.Empty()) {
            throw new IllegalStateException(
                    Messages.getString("Sequence.2") + //$NON-NLS-1$
                    Messages.getString("Sequence.3") +  //$NON-NLS-1$
                    pFirst.toString() +
                    Messages.getString("Sequence.4")); //$NON-NLS-1$
        }
        if (pNext != null) {
            pNext.Push(pFirst.Front());
        }
    }
    
    /** Return a string describing the pipeline
     * in fully parenthesized list notation. E.g., a pipeline
     * consisting of three stages A, B, and C will
     * be represented as "(A (B C))".
     *
     * @return the string describing the pipeline.
     */
    public String toString()
    {
        if (this.pFirst == null) {
            return Messages.getString("Sequence.5"); //$NON-NLS-1$
        } else {
            if (this.pNext == null) {
                return "(" + this.pFirst.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                return "(" + this.pFirst.toString() + //$NON-NLS-1$
                        " " + this.pNext.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
}
