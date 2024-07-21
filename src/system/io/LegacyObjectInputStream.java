package system.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * This is NOT legacy code!
 * 
 * A version of ObjectInputStream to read legacy SwiPred Objects
 * From https://stackoverflow.com/questions/2358886/how-can-i-deserialize-the-object-if-it-was-moved-to-another-package-or-renamed
 * @author Igor Nardin
 * @editor Benjamin Strauss
 *
 */

class LegacyObjectInputStream extends ObjectInputStream {

    public LegacyObjectInputStream(InputStream in) throws IOException {
        super(in);
    }
    
    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();

        if (resultClassDescriptor.getName().equals("bio.tools.vkabat.control.VKPred")) {
            resultClassDescriptor = ObjectStreamClass.lookup(biology.descriptor.VKPred.class);
        }

        return resultClassDescriptor;
    }
}
