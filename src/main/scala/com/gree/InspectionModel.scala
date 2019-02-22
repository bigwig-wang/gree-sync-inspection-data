package com.gree

import java.sql.Timestamp

case class SampleInspection(vendor_name: String, //enterprisename
                            vendor_code: String, //description
                            material_name: String, //partname
                            material_code: String, //partcode
                            material_group_code: String, //categoryname
                            material_group_name: String,

                            material_batch_no: String, //mlotno
                            unqualified_type: String, //faillevel
                            inspection_type: String, //qcmodename
                            status: String, //statename
                            inspection_conclusion: String, //lastcertified
                            inspector_code: String, //username code
                            inspector_name: String, //username name
                            inspection_time: Timestamp, //executeddate

                            //这几个数量到底有没有用
                            inspection_amount: Double, //qcquantity
                            unqualified_amount: Double, //failquantity
                            approval_amount: Double, //passquantity

                            base_code: String, //
                            base_name: String, //
                            department_code: String, //
                            department_name: String, //

                            is_commute: Boolean, //finalcertified
                            receiving_amount: Double, //receivedquantity
                            receiving_time: Timestamp, //delivereddate
                            parts_batch_no: String, //sjremarks
                            sending_no: String, //deliveryorderno

                            unqualified_reason_code: String, //failcode
                            unqualified_reason_name: String,

                            duty_company: String, //responsibleorganization
                            business_user: String, //purchasercode
                            remark: String,
                            create_time: Timestamp,
                            update_time: Timestamp
                           )
//remarks

case class FullInspection(vendor_name: String,
                          vendor_code: String,
                          material_name: String,
                          material_code: String,
                          material_group_code: String,
                          material_group_name: String,

                          material_batch_no: String,
                          unqualified_type: String,
                          inspection_time: Timestamp,
                          base_code: String,
                          base_name: String,
                          department_code: String,
                          department_name: String,

                          total_amount: Double,
                          total_unqualified_amount: Double,
                          total_qualified_amount: Double,
                          qualified_rate: Double,

                          unqualified_reason_code: String,
                          unqualified_reason_name: String,
                          unqualified_amount: Double,
                          unqualified_rate: Double,
                          create_time: Timestamp,
                          update_time: Timestamp
                          )

case class ApiInspection(categoryname: String,
                         partname: String,
                         partcode: String,
                         description: String,
                         enterprisename: String,
                         mlotno: String,
                         faillevel: String,
                         qcmodename: String,
                         statename: String,
                         lastcertified: String,
                         username: String,
                         executeddate: Timestamp,
                         purchasercode: String,
                         remarks: String,
                         qcquantity: Double,
                         failquantity: Double,
                         passquantity: Double,
                         failreason: String,
                         deliveryorderno: String,
                         receivedquantity: Double,
                         delivereddate: Timestamp,
                         finalcertified: String,
                         responsibleorganization: String,
                         sjremarks: String,
                         failreasoncode: String,
                         failcode: String
                         )