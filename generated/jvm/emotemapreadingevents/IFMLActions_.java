// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
// 
//      Changes to this file may cause incorrect behavior and will be lost if 
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

package emotemapreadingevents;

@net.sf.jni4net.attributes.ClrTypeInfo
public final class IFMLActions_ {
    
    //<generated-static>
    private static system.Type staticType;
    
    public static system.Type typeof() {
        return emotemapreadingevents.IFMLActions_.staticType;
    }
    
    private static void InitJNI(net.sf.jni4net.inj.INJEnv env, system.Type staticType) {
        emotemapreadingevents.IFMLActions_.staticType = staticType;
    }
    //</generated-static>
}

//<generated-proxy>
@net.sf.jni4net.attributes.ClrProxy
class __IFMLActions extends system.Object implements emotemapreadingevents.IFMLActions {
    
    protected __IFMLActions(net.sf.jni4net.inj.INJEnv __env, long __handle) {
            super(__env, __handle);
    }
    
    @net.sf.jni4net.attributes.ClrMethod("(LSystem/String;)V")
    public native void Greet(java.lang.String playerName);
    
    @net.sf.jni4net.attributes.ClrMethod("(Z)V")
    public native void CompassTool(boolean visible);
    
    @net.sf.jni4net.attributes.ClrMethod("(Z)V")
    public native void DistanceTool(boolean visible);
    
    @net.sf.jni4net.attributes.ClrMethod("(Z)V")
    public native void MapKeyTool(boolean visible);
}
//</generated-proxy>
