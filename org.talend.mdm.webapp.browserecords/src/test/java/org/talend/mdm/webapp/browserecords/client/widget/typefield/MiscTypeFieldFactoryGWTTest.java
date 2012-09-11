package org.talend.mdm.webapp.browserecords.client.widget.typefield;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings("nls")
public class MiscTypeFieldFactoryGWTTest extends GWTTestCase {

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        UserSession session = new UserSession();
        session.put(UserSession.APP_HEADER, new AppHeader());
        Registry.register(BrowseRecords.USER_SESSION, session);
    }
    
    public void testCreateField () {
        TypeModel typeModel = new SimpleTypeModel("boolean", DataTypeConstants.BOOLEAN);
        TypeFieldCreateContext context = new TypeFieldCreateContext(typeModel);
        context.setWithValue(true);
        context.setNode(new ItemNodeModel("boolean"));
        TypeFieldSource typeFieldSource = new TypeFieldSource(TypeFieldSource.FORM_INPUT);
        MiscTypeFieldFactory miscTypeFieldFactory = new MiscTypeFieldFactory(typeFieldSource, context);
        Field<?> field = miscTypeFieldFactory.createField();
        assertNotNull(field);
        assertFalse(Boolean.valueOf(field.getValue().toString()));
        assertNotNull(context.getNode());
        assertFalse(Boolean.valueOf(context.getNode().getObjectValue().toString()));
    }
   
    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords";
    }
}
