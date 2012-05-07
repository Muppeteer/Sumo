//
//  State.java
//  javaAgents
//
//  Created by Cara MacNish on 28/02/07.
//  Copyright 2007 CSSE, UWA. All rights reserved.
//

package search;
import agent.*;

public interface State {
    
  public void update (Action action) throws RuntimeException;
  
  public Actions getActions ();
  
  public Object clone ();
  
}
