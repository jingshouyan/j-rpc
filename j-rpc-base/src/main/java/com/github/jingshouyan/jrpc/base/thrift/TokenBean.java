/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.github.jingshouyan.jrpc.base.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class TokenBean implements org.apache.thrift.TBase<TokenBean, TokenBean._Fields>, java.io.Serializable, Cloneable, Comparable<TokenBean> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TokenBean");

  private static final org.apache.thrift.protocol.TField USER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("userId", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField TICKET_FIELD_DESC = new org.apache.thrift.protocol.TField("ticket", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField HEADERS_FIELD_DESC = new org.apache.thrift.protocol.TField("headers", org.apache.thrift.protocol.TType.MAP, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TokenBeanStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TokenBeanTupleSchemeFactory();

  public java.lang.String userId; // required
  public java.lang.String ticket; // required
  public java.util.Map<java.lang.String,java.lang.String> headers; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    USER_ID((short)1, "userId"),
    TICKET((short)2, "ticket"),
    HEADERS((short)3, "headers");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // USER_ID
          return USER_ID;
        case 2: // TICKET
          return TICKET;
        case 3: // HEADERS
          return HEADERS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.USER_ID, new org.apache.thrift.meta_data.FieldMetaData("userId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TICKET, new org.apache.thrift.meta_data.FieldMetaData("ticket", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.HEADERS, new org.apache.thrift.meta_data.FieldMetaData("headers", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TokenBean.class, metaDataMap);
  }

  public TokenBean() {
  }

  public TokenBean(
    java.lang.String userId,
    java.lang.String ticket,
    java.util.Map<java.lang.String,java.lang.String> headers)
  {
    this();
    this.userId = userId;
    this.ticket = ticket;
    this.headers = headers;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TokenBean(TokenBean other) {
    if (other.isSetUserId()) {
      this.userId = other.userId;
    }
    if (other.isSetTicket()) {
      this.ticket = other.ticket;
    }
    if (other.isSetHeaders()) {
      java.util.Map<java.lang.String,java.lang.String> __this__headers = new java.util.HashMap<java.lang.String,java.lang.String>(other.headers);
      this.headers = __this__headers;
    }
  }

  public TokenBean deepCopy() {
    return new TokenBean(this);
  }

  @Override
  public void clear() {
    this.userId = null;
    this.ticket = null;
    this.headers = null;
  }

  public java.lang.String getUserId() {
    return this.userId;
  }

  public TokenBean setUserId(java.lang.String userId) {
    this.userId = userId;
    return this;
  }

  public void unsetUserId() {
    this.userId = null;
  }

  /** Returns true if field userId is set (has been assigned a value) and false otherwise */
  public boolean isSetUserId() {
    return this.userId != null;
  }

  public void setUserIdIsSet(boolean value) {
    if (!value) {
      this.userId = null;
    }
  }

  public java.lang.String getTicket() {
    return this.ticket;
  }

  public TokenBean setTicket(java.lang.String ticket) {
    this.ticket = ticket;
    return this;
  }

  public void unsetTicket() {
    this.ticket = null;
  }

  /** Returns true if field ticket is set (has been assigned a value) and false otherwise */
  public boolean isSetTicket() {
    return this.ticket != null;
  }

  public void setTicketIsSet(boolean value) {
    if (!value) {
      this.ticket = null;
    }
  }

  public int getHeadersSize() {
    return (this.headers == null) ? 0 : this.headers.size();
  }

  public void putToHeaders(java.lang.String key, java.lang.String val) {
    if (this.headers == null) {
      this.headers = new java.util.HashMap<java.lang.String,java.lang.String>();
    }
    this.headers.put(key, val);
  }

  public java.util.Map<java.lang.String,java.lang.String> getHeaders() {
    return this.headers;
  }

  public TokenBean setHeaders(java.util.Map<java.lang.String,java.lang.String> headers) {
    this.headers = headers;
    return this;
  }

  public void unsetHeaders() {
    this.headers = null;
  }

  /** Returns true if field headers is set (has been assigned a value) and false otherwise */
  public boolean isSetHeaders() {
    return this.headers != null;
  }

  public void setHeadersIsSet(boolean value) {
    if (!value) {
      this.headers = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case USER_ID:
      if (value == null) {
        unsetUserId();
      } else {
        setUserId((java.lang.String)value);
      }
      break;

    case TICKET:
      if (value == null) {
        unsetTicket();
      } else {
        setTicket((java.lang.String)value);
      }
      break;

    case HEADERS:
      if (value == null) {
        unsetHeaders();
      } else {
        setHeaders((java.util.Map<java.lang.String,java.lang.String>)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case USER_ID:
      return getUserId();

    case TICKET:
      return getTicket();

    case HEADERS:
      return getHeaders();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case USER_ID:
      return isSetUserId();
    case TICKET:
      return isSetTicket();
    case HEADERS:
      return isSetHeaders();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof TokenBean)
      return this.equals((TokenBean)that);
    return false;
  }

  public boolean equals(TokenBean that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_userId = true && this.isSetUserId();
    boolean that_present_userId = true && that.isSetUserId();
    if (this_present_userId || that_present_userId) {
      if (!(this_present_userId && that_present_userId))
        return false;
      if (!this.userId.equals(that.userId))
        return false;
    }

    boolean this_present_ticket = true && this.isSetTicket();
    boolean that_present_ticket = true && that.isSetTicket();
    if (this_present_ticket || that_present_ticket) {
      if (!(this_present_ticket && that_present_ticket))
        return false;
      if (!this.ticket.equals(that.ticket))
        return false;
    }

    boolean this_present_headers = true && this.isSetHeaders();
    boolean that_present_headers = true && that.isSetHeaders();
    if (this_present_headers || that_present_headers) {
      if (!(this_present_headers && that_present_headers))
        return false;
      if (!this.headers.equals(that.headers))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetUserId()) ? 131071 : 524287);
    if (isSetUserId())
      hashCode = hashCode * 8191 + userId.hashCode();

    hashCode = hashCode * 8191 + ((isSetTicket()) ? 131071 : 524287);
    if (isSetTicket())
      hashCode = hashCode * 8191 + ticket.hashCode();

    hashCode = hashCode * 8191 + ((isSetHeaders()) ? 131071 : 524287);
    if (isSetHeaders())
      hashCode = hashCode * 8191 + headers.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(TokenBean other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetUserId()).compareTo(other.isSetUserId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userId, other.userId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetTicket()).compareTo(other.isSetTicket());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTicket()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ticket, other.ticket);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetHeaders()).compareTo(other.isSetHeaders());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHeaders()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.headers, other.headers);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("TokenBean(");
    boolean first = true;

    sb.append("userId:");
    if (this.userId == null) {
      sb.append("null");
    } else {
      sb.append(this.userId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("ticket:");
    if (this.ticket == null) {
      sb.append("null");
    } else {
      sb.append(this.ticket);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("headers:");
    if (this.headers == null) {
      sb.append("null");
    } else {
      sb.append(this.headers);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TokenBeanStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TokenBeanStandardScheme getScheme() {
      return new TokenBeanStandardScheme();
    }
  }

  private static class TokenBeanStandardScheme extends org.apache.thrift.scheme.StandardScheme<TokenBean> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TokenBean struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // USER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.userId = iprot.readString();
              struct.setUserIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TICKET
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.ticket = iprot.readString();
              struct.setTicketIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // HEADERS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map0 = iprot.readMapBegin();
                struct.headers = new java.util.HashMap<java.lang.String,java.lang.String>(2*_map0.size);
                java.lang.String _key1;
                java.lang.String _val2;
                for (int _i3 = 0; _i3 < _map0.size; ++_i3)
                {
                  _key1 = iprot.readString();
                  _val2 = iprot.readString();
                  struct.headers.put(_key1, _val2);
                }
                iprot.readMapEnd();
              }
              struct.setHeadersIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TokenBean struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.userId != null) {
        oprot.writeFieldBegin(USER_ID_FIELD_DESC);
        oprot.writeString(struct.userId);
        oprot.writeFieldEnd();
      }
      if (struct.ticket != null) {
        oprot.writeFieldBegin(TICKET_FIELD_DESC);
        oprot.writeString(struct.ticket);
        oprot.writeFieldEnd();
      }
      if (struct.headers != null) {
        oprot.writeFieldBegin(HEADERS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRING, struct.headers.size()));
          for (java.util.Map.Entry<java.lang.String, java.lang.String> _iter4 : struct.headers.entrySet())
          {
            oprot.writeString(_iter4.getKey());
            oprot.writeString(_iter4.getValue());
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TokenBeanTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TokenBeanTupleScheme getScheme() {
      return new TokenBeanTupleScheme();
    }
  }

  private static class TokenBeanTupleScheme extends org.apache.thrift.scheme.TupleScheme<TokenBean> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TokenBean struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetUserId()) {
        optionals.set(0);
      }
      if (struct.isSetTicket()) {
        optionals.set(1);
      }
      if (struct.isSetHeaders()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetUserId()) {
        oprot.writeString(struct.userId);
      }
      if (struct.isSetTicket()) {
        oprot.writeString(struct.ticket);
      }
      if (struct.isSetHeaders()) {
        {
          oprot.writeI32(struct.headers.size());
          for (java.util.Map.Entry<java.lang.String, java.lang.String> _iter5 : struct.headers.entrySet())
          {
            oprot.writeString(_iter5.getKey());
            oprot.writeString(_iter5.getValue());
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TokenBean struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.userId = iprot.readString();
        struct.setUserIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.ticket = iprot.readString();
        struct.setTicketIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TMap _map6 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.headers = new java.util.HashMap<java.lang.String,java.lang.String>(2*_map6.size);
          java.lang.String _key7;
          java.lang.String _val8;
          for (int _i9 = 0; _i9 < _map6.size; ++_i9)
          {
            _key7 = iprot.readString();
            _val8 = iprot.readString();
            struct.headers.put(_key7, _val8);
          }
        }
        struct.setHeadersIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

