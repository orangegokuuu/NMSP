package com.ws.emg.constant;

public class EsmeConstant {
	// No error
	public static final String RC_ESME_ROK  = "0";
	// Message length invalid
	public static final String RC_ESME_RINVMSGLEN = "1";
	// Command length invalid
	public static final String RC_ESME_RINVCMDLEN = "2";
	// Invalid Command ID
	public static final String RC_ESME_RINVCMDID = "3";
	// Incorrect BIND status for given command
	public static final String RC_ESME_RINVBNDSTS = "4";
	// ESME Already in Bound State
	public static final String RC_ESME_RALYBND = "5";
	// Invalid Priority Flag
	public static final String RC_ESME_RINVPRTFLG = "6";
	// Invalid Registered Delivery Flag
	public static final String RC_ESME_RINVREGDLVFLG = "7";
	// System Error
	public static final String RC_ESME_RSYSERR = "8";
	// Invalid Source Address
	public static final String RC_ESME_RINVSRCADR = "10";
	// Invalid Dest Address
	public static final String RC_ESME_RINVDTSADR = "11";
	// Message ID is invalid
	public static final String RC_ESME_RINVMSGID = "12";
	// Bind Failed
	public static final String RC_ESME_RBINDFAIL = "13";
	// Invalid Password
	public static final String RC_ESME_RINVPASWD = "14";
	// Invalid System ID
	public static final String RC_ESME_RINVSYSID = "15";
	// Reserved
	public static final String RC_ESME_RESERVED_2 = "16";
	// Cancel SM Failed
	public static final String RC_ESME_RCANCELFAIL = "17";
	// Reserved
	public static final String RC_ESME_RESERVED_3 = "18";
	// Replace SM Failed
	public static final String RC_ESME_RREPLACEFAIL = "19";
	// Message Queue Full
	public static final String RC_ESME_RMSGQFUL = "20";
	// Invalid service type
	public static final String RC_ESME_RINVSERVTYP = "21";
	// Invalid number of destinations
	public static final String RC_ESME_RINVNUMDESTS = "51";
	// Invalid distribution list name
	public static final String RC_ESME_RINVDLNAME = "52";
	// Destination flag is invalid – submit_multi
	public static final String RC_ESME_RINVDESTFLAG = "64";
	// Invalid ‘submit with replace’ request – submit_sm
	public static final String RC_ESME_RINVSUBREP = "66";
	// Invalid esm_class field data
	public static final String RC_ESME_RINVESMCLASS = "67";
	// Cannot submit to distribution list
	public static final String RC_ESME_RCNTSUBDL = "68";
	// Submit_sm or submit_multi failed
	public static final String RC_ESME_RSUBMITFAIL = "69";
	// Invalid source address ton
	public static final String RC_ESME_RINVSRCTON = "72";
	// Invalid source address npi
	public static final String RC_ESME_RINVSRCNPI = "73";
	// Invalid dest address ton
	public static final String RC_ESME_RINVDSTTON = "80";
	// Invalid dest address npi
	public static final String RC_ESME_RINVDSTNPI = "81";
	// Invalid system_type field
	public static final String RC_ESME_RINVSYSTYP = "83";
	// Invalid replace_if_present flag
	public static final String RC_ESME_RINVREPFLAG = "84";
	// Invalid number of messages
	public static final String RC_ESME_RINVNUMMSGS = "85";
	// Throttling error – ESME has exceeded allowed msg limit
	public static final String RC_ESME_RTHROTTLED = "88";
	// Invalid schedule delivery time
	public static final String RC_ESME_RINVSCHED = "97";
	// Invalid message validity period/expiry time
	public static final String RC_ESME_RINVEXPIRY = "98";
	// Predefined message invalid or not found
	public static final String RC_ESME_RINVDFTMSGID = "99";
	// ESME receiver temporary app error code
	public static final String RC_ESME_RX_T_APPN = "100";
	// ESME receiver permanent app error code
	public static final String RC_ESME_RX_P_APPN = "101";
	// ESME receiver reject message error code
	public static final String RC_ESME_RX_R_APPN = "102";
	// Query_sm request failed
	public static final String RC_ESME_RQUERYFAIL = "103";
	// Error in the optional part of the PDU body
	public static final String RC_ESME_RINVOPTPARSTREAM = "192";
	// Optional param not allowed
	public static final String RC_ESME_RINVOPTPARNOTALLWD = "193";
	// Invalid param length
	public static final String RC_ESME_RINVPARLEN = "194";
	// Expected optional param missing
	public static final String RC_ESME_RMISSINGOPTPARAM = "195";
	// Invalid optional param value
	public static final String RC_ESME_RINVOPTPARAMVAL = "196";
	// Delivery failure – data_sm_resp
	public static final String RC_ESME_RDELIVERYFAILURE = "254";
	// Unknown error
	public static final String RC_ESME_RUNKNOWNERR = "255";
	
}
