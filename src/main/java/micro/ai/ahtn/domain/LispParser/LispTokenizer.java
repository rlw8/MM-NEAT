/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package micro.ai.ahtn.domain.LispParser;

import java.io.BufferedReader;

/**
 *
 * @author santi
 */
public class LispTokenizer {
    BufferedReader br;
    int nextCharacter = -1;
    
    public LispTokenizer(BufferedReader a_br) {
        br = a_br;
    }
    
    public int nextCharacter() throws Exception {
        if (nextCharacter==-1) {
            return br.read();
        } else {
            int tmp = nextCharacter;
            nextCharacter = -1;
            return tmp;
        }
    }
    
    public String nextToken() throws Exception {
        /*
        
        tokens can be:
            (
            )
            anything else separated by spaces
        
        we ignore anything in a line after ";"
        
        we ignore anything in between "#|" and "|#
        
        */
        
        if (!br.ready()) return null;
        
        String currentToken = null;
        do {
            int c = nextCharacter();
            if (c==-1) break;
            if (c==';') {
                // skip the whole line:
                br.readLine();
                if (currentToken!=null) return currentToken;
            } else if (c==' ' || c=='\n' || c=='\r' || c=='\t') {
                if (currentToken!=null) return currentToken;
            } else if (c=='(' || c==')') {
                if (currentToken==null) {
                    return "" + (char)c;
                } else {
                    nextCharacter = c;
                    return currentToken;
                }
            } else {
                if (currentToken==null) {
                    currentToken = "" + (char)c;
                } else {
                    currentToken += (char)c;
                }
                if (currentToken.length()>=2 && currentToken.endsWith("#|")) {
                    currentToken = currentToken.substring(0, currentToken.length()-2);
                    // skip comments:
                    int previous = -1;
                    while(br.ready()) {
                        c = nextCharacter();
                        if (c==-1) break;
                        
                        if (c=='#' && previous=='|') break;
                        
                        previous = c;
                    }
                    if (currentToken.length()==0) {
                        currentToken = null;
                    } else {
                        return currentToken;                    
                    }
                }
            }
        }while(br.ready());
        
        return currentToken;
    }    
}
