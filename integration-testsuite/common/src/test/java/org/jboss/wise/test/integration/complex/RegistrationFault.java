package org.jboss.wise.test.integration.complex;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for RegistrationFault complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="RegistrationFault">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistrationFault", namespace = "http://complex.jaxws.ws.test.jboss.org/", propOrder = {
        "message"
})
@XmlSeeAlso({
        ValidationFault.class,
        AlreadyRegisteredFault.class
})
public class RegistrationFault {

    @XmlElement(required = true, nillable = true)
    protected String message;

    /**
     * Gets the value of the message property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMessage(String value) {
        this.message = value;
    }

}
