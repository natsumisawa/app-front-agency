import { Component, OnInit } from '@angular/core';
import { CountWordForm } from './report'
import { Router } from '@angular/router';
import { HttpClient, HttpHandler } from '@angular/common/http';
import { HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { InterceptorService } from '../service/interceptorService';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {

  private reportUrl = 'http://localhost:9000/api/report/count-word';

  constructor(
    private httpClient: HttpClient,
    private router: Router,
    private httpHandler: HttpHandler,
    private interceptorService: InterceptorService
  ) { }

  ngOnInit() {
  }

  // countWordForm(): void {
  //   const req = new HttpRequest('POST', this.reportUrl, this.form)
  //   this.interceptorService.intercept(req, this.httpHandler)
  //     .subscribe (res => {
  //       console.log(res);
  //     }, err => {
  //       console.log(err);
  //     })
  // }
}
