// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
// 
//      Changes to this file may cause incorrect behavior and will be lost if 
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

package emoteenercitiesmessages;

@net.sf.jni4net.attributes.ClrTypeInfo
public final class IEnercitiesGameStateEvents_ {
    
    //<generated-static>
    private static system.Type staticType;
    
    public static system.Type typeof() {
        return emoteenercitiesmessages.IEnercitiesGameStateEvents_.staticType;
    }
    
    private static void InitJNI(net.sf.jni4net.inj.INJEnv env, system.Type staticType) {
        emoteenercitiesmessages.IEnercitiesGameStateEvents_.staticType = staticType;
    }
    //</generated-static>
}

//<generated-proxy>
@net.sf.jni4net.attributes.ClrProxy
class __IEnercitiesGameStateEvents extends system.Object implements emoteenercitiesmessages.IEnercitiesGameStateEvents {
    
    protected __IEnercitiesGameStateEvents(net.sf.jni4net.inj.INJEnv __env, long __handle) {
            super(__env, __handle);
    }
    
    @net.sf.jni4net.attributes.ClrMethod("(LEmoteEnercitiesMessages/Gender;LEmoteEnercitiesMessages/Gender;)V")
    public native void PlayersGender(emoteenercitiesmessages.Gender player1Gender, emoteenercitiesmessages.Gender player2Gender);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;LSystem/String;)V")
    public native void PlayersGenderString(java.lang.String player1Gender, java.lang.String player2Gender);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;LSystem/String;LSystem/String;LSystem/String;)V")
    public native void GameStarted(java.lang.String player1Name, java.lang.String player1Role, java.lang.String player2Name, java.lang.String player2Role);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;LSystem/String;LSystem/String;LSystem/String;LSystem/String;)V")
    public native void ResumeGame(java.lang.String player1Name, java.lang.String player1Role, java.lang.String player2Name, java.lang.String player2Role, java.lang.String serializedGameState);
    
    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    public native void EndGameSuccessfull(int totalScore);
    
    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    public native void EndGameNoOil(int totalScore);
    
    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    public native void EndGameTimeOut(int totalScore);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;)V")
    public native void TurnChanged(java.lang.String serializedGameState);
    
    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    public native void ReachedNewLevel(int level);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;LSystem/String;LSystem/String;LSystem/String;)V")
    public native void StrategyGameMoves(java.lang.String environmentalistMove, java.lang.String economistMove, java.lang.String mayorMove, java.lang.String globalMove);
}
//</generated-proxy>