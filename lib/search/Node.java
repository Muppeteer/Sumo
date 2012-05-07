//
//  Node.java
//  javaAgents
//
//  Created by Cara MacNish on 28/02/07.
//  Copyright (c) 2007 CSSE, UWA. All rights reserved.
//

package search;
import agent.*;

/**
 * Note: direct access to instance variables is deprecated. Use get/set methods.
 */
public class Node {

  public State state;
//  public State lastState;  not used anywhere? ckm
  public Actions path;
  public double cost;
  public double utility;  // provided for efficiency only

  public Node (State state) {
    this.state = state;
    this.path = new Actions();
    this.cost = 0;
    this.utility = 0;
//    this.lastState = null;
  }

  public Node (State state, Actions path) {
    this.state = state;
    this.path = path;
    this.cost = 0;
    this.utility = 0;
  }

  public Node (State state, Actions path, double cost, double utility) {
    this.state = state;
    this.path = path;
    this.cost = cost;
    this.utility = utility;
  }

  public Object clone () {
    return new Node((State) state.clone(), (Actions) path.clone(), cost, utility);
  }

  public void update (Action action) {
    state.update(action);
    path.add(action);
    cost = cost + action.getCost();
  }
  
  public State getState () {return state;}  
  public void setState (State state) {this.state = state;}

  public Actions getPath () {return path;}  
  public void setPath (Actions path) {this.path = path;}

  public double getCost () {return cost;}  
  public void setCost (double cost) {this.cost = cost;}

  public double getUtility () {return utility;}  
  public void setUtility (double utility) {this.utility = utility;}

}
