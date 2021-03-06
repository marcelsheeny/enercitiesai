// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
// 
//      Changes to this file may cause incorrect behavior and will be lost if 
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

package emoteenercitiesmessages;

@net.sf.jni4net.attributes.ClrInterface
public interface IEnercitiesGameStateEvents {
    
    //<generated-interface>
    @net.sf.jni4net.attributes.ClrMethod("(LEmoteEnercitiesMessages/Gender;LEmoteEnercitiesMessages/Gender;)V")
    void PlayersGender(emoteenercitiesmessages.Gender player1Gender, emoteenercitiesmessages.Gender player2Gender);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;LSystem/String;)V")
    void PlayersGenderString(java.lang.String player1Gender, java.lang.String player2Gender);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;LSystem/String;LSystem/String;LSystem/String;)V")
    void GameStarted(java.lang.String player1Name, java.lang.String player1Role, java.lang.String player2Name, java.lang.String player2Role);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;LSystem/String;LSystem/String;LSystem/String;LSystem/String;)V")
    void ResumeGame(java.lang.String player1Name, java.lang.String player1Role, java.lang.String player2Name, java.lang.String player2Role, java.lang.String serializedGameState);
    
    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    void EndGameSuccessfull(int totalScore);
    
    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    void EndGameNoOil(int totalScore);
    
    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    void EndGameTimeOut(int totalScore);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;)V")
    void TurnChanged(java.lang.String serializedGameState);
    
    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    void ReachedNewLevel(int level);
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;LSystem/String;LSystem/String;LSystem/String;)V")
    void StrategyGameMoves(java.lang.String environmentalistMove, java.lang.String economistMove, java.lang.String mayorMove, java.lang.String globalMove);
    //</generated-interface>
}
